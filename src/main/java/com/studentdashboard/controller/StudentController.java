package com.studentdashboard.controller;

import com.studentdashboard.model.Student;
import com.studentdashboard.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        model.addAttribute("student", student);
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        model.addAttribute("student", student);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication,
                                @ModelAttribute Student updatedStudent) {
        String email = authentication.getName();
        Student currentStudent = studentService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        studentService.updateProfile(currentStudent.getId(), updatedStudent);
        return "redirect:/student/dashboard?updated";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication authentication, Model model) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        model.addAttribute("student", student);
        return "schedule";
    }

    @GetMapping("/gradebook")
    public String gradebook(Authentication authentication, Model model) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        model.addAttribute("student", student);
        return "gradebook";
    }

    @GetMapping("/news")
    public String news(Authentication authentication, Model model) {
        String email = authentication.getName();
        Student student = studentService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        model.addAttribute("student", student);
        return "news";
    }
}