package com.studentdashboard.controller;

import com.studentdashboard.model.Student;
import com.studentdashboard.repository.GradeRepository;
import com.studentdashboard.repository.SubjectRepository;
import com.studentdashboard.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.studentdashboard.service.SqlService;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private GradeRepository gradeRepository;
    @Autowired private SubjectRepository subjectRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("student", student);
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
    public String gradebook(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("grades", gradeRepository.findByStudentId(student.getId()));
        model.addAttribute("subjects", subjectRepository.findAll());
        return "student/gradebook";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("subjects", subjectRepository.findAll());
        return "student/schedule";
    }

    @Autowired
    private SqlService sqlService;
    @GetMapping("/news")
    public String news(Authentication auth, Model model) {
        Student student = studentService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("newsList", sqlService.getAllNews());
        return "student/news";
    }

}