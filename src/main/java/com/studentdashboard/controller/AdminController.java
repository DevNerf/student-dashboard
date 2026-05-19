package com.studentdashboard.controller;

import com.studentdashboard.model.*;
import com.studentdashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.studentdashboard.service.CriteriaQueryService;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private StudentRepository studentRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private GradeRepository gradeRepository;
    @Autowired private NewsRepository newsRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private StudentSubjectRepository studentSubjectRepository;
    @Autowired private CriteriaQueryService criteriaQueryService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", studentRepository.count());
        model.addAttribute("groupCount", groupRepository.count());
        model.addAttribute("subjectCount", subjectRepository.count());
        model.addAttribute("newsCount", newsRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/students")
    public String students(@RequestParam(required = false) Long groupId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        Page<Student> studentPage;

        if (groupId != null) {
            studentPage = studentRepository.findByGroupId(groupId, pageable);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("groups", groupRepository.findAll());
        return "admin/students";
    }

    @GetMapping("/students/create")
    public String createForm(Model model) {
        model.addAttribute("groups", groupRepository.findAll());
        return "admin/student-form";
    }

    @PostMapping("/students/save")
    public String save(@RequestParam String firstName, @RequestParam String lastName,
                       @RequestParam(required = false) String middleName,
                       @RequestParam String email, @RequestParam String password,
                       @RequestParam(required = false) String birthPlace,
                       @RequestParam(required = false) Long groupId,
                       @RequestParam(required = false) String faculty,
                       Model model) {
        if (studentRepository.existsByEmail(email)) {
            model.addAttribute("error", "Студент с таким email уже существует!");
            model.addAttribute("groups", groupRepository.findAll());
            return "admin/student-form";
        }

        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setMiddleName(middleName);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password));
        student.setBirthPlace(birthPlace);
        student.setFaculty(faculty);
        student.setRole("STUDENT");
        if (groupId != null) {
            student.setGroup(groupRepository.findById(groupId).orElse(null));
        }
        studentRepository.save(student);
        return "redirect:/admin/students?success";
    }

    @GetMapping("/students/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id).orElseThrow();
        if (student.getGroup() != null) {
            student.getGroup().getName();
        }
        model.addAttribute("student", student);
        model.addAttribute("groups", groupRepository.findAll());
        return "admin/student-form";
    }

    @PostMapping("/students/update/{id}")
    public String update(@PathVariable Long id, @RequestParam String firstName,
                         @RequestParam String lastName,
                         @RequestParam(required = false) String middleName,
                         @RequestParam String email,
                         @RequestParam(required = false) String password,
                         @RequestParam(required = false) String birthPlace,
                         @RequestParam(required = false) Long groupId,
                         @RequestParam(required = false) String faculty) {
        Student student = studentRepository.findById(id).orElseThrow();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setMiddleName(middleName);
        student.setEmail(email);
        student.setBirthPlace(birthPlace);
        student.setFaculty(faculty);
        if (password != null && !password.isEmpty()) {
            student.setPassword(passwordEncoder.encode(password));
        }
        if (groupId != null) {
            student.setGroup(groupRepository.findById(groupId).orElse(null));
        }
        studentRepository.save(student);
        return "redirect:/admin/students?success";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/admin/students?success";
    }

    @GetMapping("/groups")
    public String groups(Model model) {
        model.addAttribute("groups", groupRepository.findAll());
        return "admin/groups";
    }

    @PostMapping("/groups/save")
    public String saveGroup(@RequestParam String name, Model model) {
        if (groupRepository.findByName(name).isPresent()) {
            model.addAttribute("error", "Группа с названием '" + name + "' уже существует!");
            model.addAttribute("groups", groupRepository.findAll());
            return "admin/groups";
        }
        GroupEntity group = new GroupEntity();
        group.setName(name);
        groupRepository.save(group);
        return "redirect:/admin/groups?success";
    }

    @GetMapping("/groups/delete/{id}")
    public String deleteGroup(@PathVariable Long id) {
        List<Student> students = studentRepository.findByGroupId(id);
        for (Student s : students) {
            s.setGroup(null);
            studentRepository.save(s);
        }
        groupRepository.deleteById(id);
        return "redirect:/admin/groups?success";
    }

    @GetMapping("/subjects")
    public String subjects(Model model) {
        model.addAttribute("subjects", subjectRepository.findAll());
        return "admin/subjects";
    }

    @PostMapping("/subjects/save")
    public String saveSubject(@RequestParam String name,
                              @RequestParam(required = false) String teacherLastName,
                              @RequestParam(required = false) String teacherFirstName,
                              @RequestParam(required = false) String teacherMiddleName,
                              @RequestParam(required = false) Integer semester,
                              @RequestParam(required = false) String examType) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setSemester(semester);
        subject.setExamType(examType);
        String teacher = "";
        if (teacherLastName != null) teacher += teacherLastName;
        if (teacherFirstName != null) teacher += " " + teacherFirstName;
        if (teacherMiddleName != null) teacher += " " + teacherMiddleName;
        subject.setTeacher(teacher.trim());
        subjectRepository.save(subject);
        return "redirect:/admin/subjects?success";
    }

    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectRepository.deleteById(id);
        return "redirect:/admin/subjects?success";
    }

    @GetMapping("/grades")
    public String gradesForm(@RequestParam(required = false) Long groupId,
                             @RequestParam(required = false) Long subjectId,
                             @RequestParam(defaultValue = "1") int semester,
                             Model model) {
        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findBySemester(semester));
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("selectedGroupId", groupId);
        model.addAttribute("selectedSubjectId", subjectId);

        if (groupId != null && subjectId != null) {
            //model.addAttribute("students", studentRepository.findByGroupId(groupId));
            List<Student> students = studentRepository.findByGroupId(groupId);
            students.sort(Comparator.comparing(Student::getLastName));
            model.addAttribute("students", students);
            model.addAttribute("selectedGroupId", groupId);
            model.addAttribute("selectedSubjectId", subjectId);

            List<Grade> existingGrades = gradeRepository.findBySubjectId(subjectId);
            Map<Long, Integer> gradeMap = new HashMap<>();
            for (Grade g : existingGrades) {
                if (g.getStudent().getGroup() != null && g.getStudent().getGroup().getId().equals(groupId)) {
                    gradeMap.put(g.getStudent().getId(), g.getValue());
                }
            }
            model.addAttribute("gradeMap", gradeMap);
        }
        return "admin/grades";
    }

    @PostMapping("/grades/save")
    public String saveGrades(@RequestParam Long subjectId, @RequestParam Long groupId,
                             @RequestParam(defaultValue = "1") int semester,
                             @RequestParam Map<String, String> params) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();
        params.forEach((key, value) -> {
            if (key.startsWith("grade_") && value != null && !value.trim().isEmpty()) {
                try {
                    Long studentId = Long.parseLong(key.replace("grade_", ""));
                    int gradeValue = Integer.parseInt(value.trim());
                    if (gradeValue >= 0 && gradeValue <= 100) {
                        Student student = studentRepository.findById(studentId).orElse(null);
                        if (student != null) {
                            List<Grade> existingGrades = gradeRepository.findByStudentIdAndSubjectId(studentId, subjectId);
                            gradeRepository.deleteAll(existingGrades);

                            Grade grade = new Grade();
                            grade.setStudent(student);
                            grade.setSubject(subject);
                            grade.setValue(gradeValue);
                            grade.setDate(LocalDate.now());
                            gradeRepository.save(grade);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        });
        return "redirect:/admin/grades?success&groupId=" + groupId + "&subjectId=" + subjectId + "&semester=" + semester;
    }

    @PostMapping("/grades/save-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveGradesAjax(@RequestParam Long subjectId,
                                                              @RequestParam Long groupId,
                                                              @RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = new ArrayList<>();

        try {
            Subject subject = subjectRepository.findById(subjectId).orElseThrow();
            int savedCount = 0;

            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.startsWith("grade_") && value != null && !value.trim().isEmpty()) {
                    try {
                        Long studentId = Long.parseLong(key.replace("grade_", ""));
                        int gradeValue = Integer.parseInt(value.trim());

                        if (gradeValue < 0 || gradeValue > 100) {
                            errors.add("Баллы должны быть от 0 до 100!");
                            continue;
                        }

                        Student student = studentRepository.findById(studentId).orElse(null);
                        if (student != null) {
                            List<Grade> existingGrades = gradeRepository.findByStudentIdAndSubjectId(studentId, subjectId);
                            gradeRepository.deleteAll(existingGrades);

                            Grade grade = new Grade();
                            grade.setStudent(student);
                            grade.setSubject(subject);
                            grade.setValue(gradeValue);
                            grade.setDate(LocalDate.now());
                            gradeRepository.save(grade);
                            savedCount++;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            if (!errors.isEmpty()) {
                response.put("success", false);
                response.put("message", String.join("; ", errors));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Сохранено оценок: " + savedCount);
            response.put("savedCount", savedCount);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/news")
    public String news(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<News> newsPage = newsRepository.findAllByOrderByPublishDateDesc(pageable);
        model.addAttribute("newsList", newsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", newsPage.getTotalPages());
        return "admin/news";
    }

    @GetMapping("/news/create")
    public String createNewsForm() {
        return "admin/news-form";
    }

    @PostMapping("/news/save")
    public String saveNews(@RequestParam String title, @RequestParam String content,
                           @RequestParam(required = false) String category) {
        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setCategory(category);
        news.setPublishDate(LocalDate.now());
        newsRepository.save(news);
        return "redirect:/admin/news?success";
    }

    @GetMapping("/news/edit/{id}")
    public String editNews(@PathVariable Long id, Model model) {
        model.addAttribute("news", newsRepository.findById(id).orElseThrow());
        return "admin/news-form";
    }

    @PostMapping("/news/update/{id}")
    public String updateNews(@PathVariable Long id, @RequestParam String title,
                             @RequestParam String content, @RequestParam(required = false) String category) {
        News news = newsRepository.findById(id).orElseThrow();
        news.setTitle(title);
        news.setContent(content);
        news.setCategory(category);
        newsRepository.save(news);
        return "redirect:/admin/news?success";
    }

    @GetMapping("/news/delete/{id}")
    public String deleteNews(@PathVariable Long id) {
        newsRepository.deleteById(id);
        return "redirect:/admin/news?success";
    }

    @GetMapping("/electives")
    public String electives(@RequestParam(required = false) Long groupId,
                            @RequestParam(required = false) Long studentId,
                            @RequestParam(defaultValue = "1") int semester,
                            @RequestParam(required = false) Long subjectId,
                            Model model) {
        if (groupId != null) {
            //model.addAttribute("students", studentRepository.findByGroupId(groupId));
            List<Student> students = studentRepository.findByGroupId(groupId);
            students.sort(Comparator.comparing(Student::getLastName));
            model.addAttribute("students", students);
            model.addAttribute("selectedGroupId", groupId);
        } else {
            model.addAttribute("students", studentRepository.findAll());
        }

        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findBySemester(semester));
        model.addAttribute("selectedSemester", semester);

        if (studentId != null) {
            Student student = studentRepository.findById(studentId).orElseThrow();
            model.addAttribute("selectedStudent", student);
            List<StudentSubject> electives = studentSubjectRepository.findByStudentIdAndSemester(studentId, semester);
            model.addAttribute("studentElectives", electives);
            model.addAttribute("electiveCount", electives.size());
        }

        if (subjectId != null) {
            List<Student> students = studentSubjectRepository.findStudentsBySubjectAndSemester(subjectId, semester);
            model.addAttribute("subjectStudents", students);
            model.addAttribute("selectedSubject", subjectRepository.findById(subjectId).orElse(null));
        }

        model.addAttribute("studentsWithoutElectives", criteriaQueryService.findStudentsWithoutElectives(semester, groupId));
        model.addAttribute("popularSubjects", criteriaQueryService.findPopularSubjects(semester, 2));

        return "admin/electives";
    }

    @PostMapping("/electives/save")
    public String saveElectives(@RequestParam Long studentId,
                                @RequestParam int semester,
                                @RequestParam(required = false) List<Long> subjectIds,
                                @RequestParam(defaultValue = "5") int maxElectives) {
        if (subjectIds != null && subjectIds.size() > maxElectives) {
            return "redirect:/admin/electives?error=limit&studentId=" + studentId + "&semester=" + semester;
        }

        studentSubjectRepository.deleteAll(
                studentSubjectRepository.findByStudentIdAndSemester(studentId, semester)
        );

        if (subjectIds != null) {
            Student student = studentRepository.findById(studentId).orElseThrow();
            for (Long subjId : subjectIds) {
                Subject subject = subjectRepository.findById(subjId).orElseThrow();
                StudentSubject ss = new StudentSubject();
                ss.setStudent(student);
                ss.setSubject(subject);
                ss.setSemester(semester);
                studentSubjectRepository.save(ss);
            }
        }

        return "redirect:/admin/electives?success&studentId=" + studentId + "&semester=" + semester;
    }
}