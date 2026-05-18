package com.studentdashboard.controller;

import com.studentdashboard.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SqlService sqlService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", sqlService.countStudents());
        model.addAttribute("groupCount", sqlService.countGroups());
        model.addAttribute("subjectCount", sqlService.countSubjects());
        model.addAttribute("newsCount", sqlService.countNews());  // ← добавить сюда
        return "admin/dashboard";
    }

    @GetMapping("/students")
    public String students(@RequestParam(required = false) Long groupId, Model model) {
        model.addAttribute("students", groupId != null ?
                sqlService.getStudentsByGroup(groupId) : sqlService.getAllStudents());
        model.addAttribute("groups", sqlService.getAllGroups());
        return "admin/students";
    }

    @GetMapping("/students/create")
    public String createForm(Model model) {
        model.addAttribute("groups", sqlService.getAllGroups());
        return "admin/student-form";
    }

    @PostMapping("/students/save")
    public String save(@RequestParam String firstName, @RequestParam String lastName,
                       @RequestParam String email, @RequestParam String password,
                       @RequestParam(required = false) String birthPlace,
                       @RequestParam(required = false) Long groupId,
                       @RequestParam(required = false) String faculty) {
        sqlService.createStudent(firstName, lastName, email, password, birthPlace, groupId, faculty);
        return "redirect:/admin/students?success";
    }

    @GetMapping("/students/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("student", sqlService.getStudentById(id));
        model.addAttribute("groups", sqlService.getAllGroups());
        return "admin/student-form";
    }

    @PostMapping("/students/update/{id}")
    public String update(@PathVariable Long id, @RequestParam String firstName,
                         @RequestParam String lastName, @RequestParam String email,
                         @RequestParam(required = false) String password,
                         @RequestParam(required = false) String birthPlace,
                         @RequestParam(required = false) Long groupId,
                         @RequestParam(required = false) String faculty) {
        sqlService.updateStudent(id, firstName, lastName, email, password, birthPlace, groupId, faculty);
        return "redirect:/admin/students?success";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        sqlService.deleteStudent(id);
        return "redirect:/admin/students?success";
    }

    @GetMapping("/groups")
    public String groups(Model model) {
        model.addAttribute("groups", sqlService.getAllGroups());
        return "admin/groups";
    }

    @PostMapping("/groups/save")
    public String saveGroup(@RequestParam String name) {
        sqlService.createGroup(name);
        return "redirect:/admin/groups?success";
    }

    @GetMapping("/groups/delete/{id}")
    public String deleteGroup(@PathVariable Long id) {
        sqlService.deleteGroup(id);
        return "redirect:/admin/groups?success";
    }

    @GetMapping("/subjects")
    public String subjects(Model model) {
        model.addAttribute("subjects", sqlService.getAllSubjects());
        return "admin/subjects";
    }

    @PostMapping("/subjects/save")
    public String saveSubject(@RequestParam String name,
                              @RequestParam(required = false) String teacherLastName,
                              @RequestParam(required = false) String teacherFirstName,
                              @RequestParam(required = false) String teacherMiddleName,
                              @RequestParam(required = false) Integer semester,
                              @RequestParam(required = false) String examType) {
        sqlService.createSubject(name, teacherLastName, teacherFirstName, teacherMiddleName, semester, examType);
        return "redirect:/admin/subjects?success";
    }

    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        sqlService.deleteSubject(id);
        return "redirect:/admin/subjects?success";
    }

    @GetMapping("/grades")
    public String gradesForm(@RequestParam(required = false) Long groupId,
                             @RequestParam(required = false) Long subjectId, Model model) {
        model.addAttribute("groups", sqlService.getAllGroups());
        model.addAttribute("subjects", sqlService.getAllSubjects());

        if (groupId != null && subjectId != null) {
            model.addAttribute("students", sqlService.getStudentsByGroup(groupId));
            model.addAttribute("selectedGroupId", groupId);
            model.addAttribute("selectedSubjectId", subjectId);

            List<Map<String, Object>> existingGrades = sqlService.getGradesByGroupAndSubject(groupId, subjectId);
            Map<Long, Integer> gradeMap = new HashMap<>();
            for (Map<String, Object> g : existingGrades) {
                Number studentIdNum = (Number) g.get("student_id");
                Integer value = (Integer) g.get("value");
                gradeMap.put(studentIdNum.longValue(), value);
            }
            model.addAttribute("gradeMap", gradeMap);
        }
        return "admin/grades";
    }

    @PostMapping("/grades/save")
    public String saveGrades(@RequestParam Long subjectId, @RequestParam Long groupId,
                             @RequestParam Map<String, String> params) {
        params.forEach((key, value) -> {
            if (key.startsWith("grade_") && value != null && !value.trim().isEmpty()) {
                try {
                    Long studentId = Long.parseLong(key.replace("grade_", ""));
                    int gradeValue = Integer.parseInt(value.trim());
                    if (gradeValue >= 2 && gradeValue <= 5) {
                        sqlService.saveGrade(studentId, subjectId, gradeValue);
                    }
                } catch (NumberFormatException ignored) {}
            }
        });
        return "redirect:/admin/grades?success&groupId=" + groupId + "&subjectId=" + subjectId;
    }

    @GetMapping("/news")
    public String news(Model model) {
        model.addAttribute("newsList", sqlService.getAllNews());
        return "admin/news";
    }

    @GetMapping("/news/create")
    public String createNewsForm() {
        return "admin/news-form";
    }

    @PostMapping("/news/save")
    public String saveNews(@RequestParam String title,
                           @RequestParam String content,
                           @RequestParam(required = false) String category) {
        sqlService.createNews(title, content, category);
        return "redirect:/admin/news?success";
    }

    @GetMapping("/news/edit/{id}")
    public String editNews(@PathVariable Long id, Model model) {
        model.addAttribute("news", sqlService.getNewsById(id));
        return "admin/news-form";
    }

    @PostMapping("/news/update/{id}")
    public String updateNews(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(required = false) String category) {
        sqlService.updateNews(id, title, content, category);
        return "redirect:/admin/news?success";
    }

    @GetMapping("/news/delete/{id}")
    public String deleteNews(@PathVariable Long id) {
        sqlService.deleteNews(id);
        return "redirect:/admin/news?success";
    }
}