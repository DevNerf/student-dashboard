package com.studentdashboard.repository;

import com.studentdashboard.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findBySemester(Integer semester);  // ← добавить
}