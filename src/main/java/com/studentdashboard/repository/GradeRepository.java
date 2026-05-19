package com.studentdashboard.repository;

import com.studentdashboard.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findBySubjectId(Long subjectId);
    List<Grade> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<Grade> findByStudentIdOrderByDateDesc(Long studentId);
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId ORDER BY g.date DESC, g.id DESC")
    List<Grade> findRecentGradesByStudentId(@Param("studentId") Long studentId);
}