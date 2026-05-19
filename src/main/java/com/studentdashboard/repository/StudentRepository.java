package com.studentdashboard.repository;

import com.studentdashboard.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Student> findByGroupId(Long groupId);
    Page<Student> findByGroupId(Long groupId, Pageable pageable);  // ← добавить
}