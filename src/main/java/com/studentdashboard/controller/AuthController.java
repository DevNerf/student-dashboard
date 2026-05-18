package com.studentdashboard.controller;

import com.studentdashboard.model.Student;
import com.studentdashboard.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("student", new Student());
        return "login";
    }
}