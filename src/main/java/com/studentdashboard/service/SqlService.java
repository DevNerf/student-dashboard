package com.studentdashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Map;

@Service
public class SqlService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createGroup(String name) {
        jdbcTemplate.update("INSERT INTO student_groups (name) VALUES (?)", name);
    }

    public void deleteGroup(Long id) {
        jdbcTemplate.update("DELETE FROM student_groups WHERE id = ?", id);
    }

    public List<Map<String, Object>> getAllGroups() {
        return jdbcTemplate.queryForList("SELECT * FROM student_groups ORDER BY name");
    }

    public void createStudent(String firstName, String lastName, String email,
                              String password, String birthPlace, Long groupId, String faculty) {
        jdbcTemplate.update(
                "INSERT INTO students (first_name, last_name, email, password, birth_place, group_id, faculty, role) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 'STUDENT')",
                firstName, lastName, email, passwordEncoder.encode(password), birthPlace, groupId, faculty
        );
    }

    public void updateStudent(Long id, String firstName, String lastName, String email,
                              String password, String birthPlace, Long groupId, String faculty) {
        if (password != null && !password.isEmpty()) {
            jdbcTemplate.update(
                    "UPDATE students SET first_name=?, last_name=?, email=?, password=?, birth_place=?, group_id=?, faculty=? WHERE id=?",
                    firstName, lastName, email, passwordEncoder.encode(password), birthPlace, groupId, faculty, id
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE students SET first_name=?, last_name=?, email=?, birth_place=?, group_id=?, faculty=? WHERE id=?",
                    firstName, lastName, email, birthPlace, groupId, faculty, id
            );
        }
    }

    public void deleteStudent(Long id) {
        jdbcTemplate.update("DELETE FROM grades WHERE student_id = ?", id);
        jdbcTemplate.update("DELETE FROM students WHERE id = ?", id);
    }

    public List<Map<String, Object>> getStudentsByGroup(Long groupId) {
        String sql = """
            SELECT s.*, g.name as group_name 
            FROM students s 
            LEFT JOIN student_groups g ON s.group_id = g.id 
            WHERE s.group_id = ? 
            ORDER BY s.last_name
            """;
        return jdbcTemplate.queryForList(sql, groupId);
    }

    public List<Map<String, Object>> getAllStudents() {
        String sql = """
            SELECT s.*, g.name as group_name 
            FROM students s 
            LEFT JOIN student_groups g ON s.group_id = g.id 
            ORDER BY s.last_name
            """;
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> getStudentById(Long id) {
        String sql = """
            SELECT s.*, g.name as group_name 
            FROM students s 
            LEFT JOIN student_groups g ON s.group_id = g.id 
            WHERE s.id = ?
            """;
        return jdbcTemplate.queryForMap(sql, id);
    }

    public void createSubject(String name, String teacherLastName, String teacherFirstName,
                              String teacherMiddleName, Integer semester, String examType) {
        String teacher = "";
        if (teacherLastName != null) teacher += teacherLastName;
        if (teacherFirstName != null) teacher += " " + teacherFirstName;
        if (teacherMiddleName != null) teacher += " " + teacherMiddleName;
        teacher = teacher.trim();

        jdbcTemplate.update(
                "INSERT INTO subjects (name, teacher, semester, exam_type) VALUES (?, ?, ?, ?)",
                name, teacher.isEmpty() ? null : teacher, semester, examType
        );
    }

    public void deleteSubject(Long id) {
        jdbcTemplate.update("DELETE FROM grades WHERE subject_id = ?", id);
        jdbcTemplate.update("DELETE FROM subjects WHERE id = ?", id);
    }

    public List<Map<String, Object>> getAllSubjects() {
        return jdbcTemplate.queryForList("SELECT * FROM subjects ORDER BY name");
    }

    public void saveGrade(Long studentId, Long subjectId, Integer value) {
        // Удаляем старую оценку
        jdbcTemplate.update("DELETE FROM grades WHERE student_id = ? AND subject_id = ?", studentId, subjectId);
        // Вставляем новую
        jdbcTemplate.update(
                "INSERT INTO grades (student_id, subject_id, value, date) VALUES (?, ?, ?, CURRENT_DATE)",
                studentId, subjectId, value
        );
    }

    public List<Map<String, Object>> getGradesByStudent(Long studentId) {
        String sql = """
            SELECT g.*, s.name as subject_name, s.teacher, s.exam_type 
            FROM grades g 
            JOIN subjects s ON g.subject_id = s.id 
            WHERE g.student_id = ? 
            ORDER BY g.date DESC
            """;
        return jdbcTemplate.queryForList(sql, studentId);
    }

    public List<Map<String, Object>> getGradesByGroupAndSubject(Long groupId, Long subjectId) {
        String sql = """
            SELECT g.value, g.student_id 
            FROM grades g 
            WHERE g.subject_id = ? AND g.student_id IN (SELECT id FROM students WHERE group_id = ?)
            """;
        return jdbcTemplate.queryForList(sql, subjectId, groupId);
    }

    public void createNews(String title, String content, String category) {
        jdbcTemplate.update(
                "INSERT INTO news (title, content, publish_date, category) VALUES (?, ?, CURRENT_DATE, ?)",
                title, content, category
        );
    }

    public void updateNews(Long id, String title, String content, String category) {
        jdbcTemplate.update(
                "UPDATE news SET title=?, content=?, category=? WHERE id=?",
                title, content, category, id
        );
    }

    public void deleteNews(Long id) {
        jdbcTemplate.update("DELETE FROM news WHERE id = ?", id);
    }

    public List<Map<String, Object>> getAllNews() {
        return jdbcTemplate.queryForList("SELECT * FROM news ORDER BY publish_date DESC");
    }

    public Map<String, Object> getNewsById(Long id) {
        return jdbcTemplate.queryForMap("SELECT * FROM news WHERE id = ?", id);
    }

    public int countNews() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM news", Integer.class);
    }

    public int countStudents() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students", Integer.class);
    }

    public int countGroups() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM student_groups", Integer.class);
    }

    public int countSubjects() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM subjects", Integer.class);
    }
}