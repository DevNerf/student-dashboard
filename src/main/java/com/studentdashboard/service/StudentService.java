package com.studentdashboard.service;

import com.studentdashboard.model.Student;
import com.studentdashboard.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student createStudent(Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setRole("STUDENT");
        return studentRepository.save(student);
    }

    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Student updateStudent(Student updated) {
        Student existing = studentRepository.findById(updated.getId()).orElseThrow();
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setBirthPlace(updated.getBirthPlace());
        existing.setFaculty(updated.getFaculty());
        existing.setGroup(updated.getGroup());
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
        return studentRepository.save(existing);
    }

    public void changePassword(Long id, String newPassword) {
        Student student = studentRepository.findById(id).orElseThrow();
        student.setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}