package com.studentdashboard.controller;

import com.studentdashboard.model.Student;
import com.studentdashboard.model.Grade;
import com.studentdashboard.repository.GradeRepository;
import com.studentdashboard.repository.SubjectRepository;
import com.studentdashboard.repository.StudentSubjectRepository;
import com.studentdashboard.model.StudentSubject;
import com.studentdashboard.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.studentdashboard.repository.NewsRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private GradeRepository gradeRepository;
    @Autowired private SubjectRepository subjectRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();

        //List<Grade> allGrades = gradeRepository.findByStudentIdOrderByDateDesc(student.getId());
        List<Grade> allGrades = gradeRepository.findRecentGradesByStudentId(student.getId());
        List<Grade> recentGrades = allGrades.size() > 5 ? allGrades.subList(0, 5) : allGrades;

        double avg = 0;
        if (!allGrades.isEmpty()) {
            avg = allGrades.stream().mapToInt(Grade::getValue).average().orElse(0);
        }

        model.addAttribute("student", student);
        model.addAttribute("recentGrades", recentGrades);
        model.addAttribute("averageGrade", avg > 0 ? String.format("%.1f", avg) : null);
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("readonly", true);
        return "student/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(Authentication auth, @RequestParam String newPassword) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        studentService.changePassword(student.getId(), newPassword);
        return "redirect:/student/dashboard?passwordChanged";
    }

    @GetMapping("/gradebook")
    public String gradebook(Authentication auth,
                            @RequestParam(defaultValue = "1") int semester,
                            Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();

        List<Grade> allGrades = gradeRepository.findByStudentIdOrderByDateDesc(student.getId());
        List<Grade> filteredGrades = allGrades.stream()
                .filter(g -> g.getSubject().getSemester() == null || g.getSubject().getSemester() == semester)
                .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("grades", filteredGrades);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("subjects", subjectRepository.findBySemester(semester));
        return "student/gradebook";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication auth,
                           @RequestParam(defaultValue = "1") int semester,
                           Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("subjects", subjectRepository.findBySemester(semester));
        return "student/schedule";
    }

    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("/news")
    public String news(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("newsList", newsRepository.findAllByOrderByPublishDateDesc(Pageable.unpaged()).getContent());
        return "student/news";
    }

    @Autowired
    private StudentSubjectRepository studentSubjectRepository;

    @GetMapping("/electives")
    public String electives(Authentication auth, @RequestParam(defaultValue = "1") int semester, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        List<StudentSubject> electives = studentSubjectRepository.findByStudentIdAndSemester(student.getId(), semester);
        model.addAttribute("student", student);
        model.addAttribute("electives", electives);
        model.addAttribute("selectedSemester", semester);
        return "student/electives";
    }
}