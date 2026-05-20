package com.studentdashboard.controller;

import com.studentdashboard.model.Student;
import com.studentdashboard.model.Admin;
import com.studentdashboard.repository.StudentRepository;
import com.studentdashboard.repository.AdminRepository;
import com.studentdashboard.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("student", new Student());
        return "login";
    }

    @GetMapping("/admin-register")
    public String adminRegisterForm(Model model) {
        model.addAttribute("student", new Admin());
        return "admin-register";
    }

    @PostMapping("/admin-register")
    public String adminRegister(@Valid @ModelAttribute("student") Admin admin,
                                @RequestParam String secretKey,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin-register";
        }

        if (!adminSecretKey.equals(secretKey)) {
            model.addAttribute("error", "Неверный секретный ключ!");
            return "admin-register";
        }

        if (adminRepository.existsByEmail(admin.getEmail())) {
            model.addAttribute("error", "Email уже используется!");
            return "admin-register";
        }

        admin.setRole("ADMIN");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        logger.info("Регистрация нового админа: email={}", admin.getEmail());
        return "redirect:/login?admin-registered";
    }
}