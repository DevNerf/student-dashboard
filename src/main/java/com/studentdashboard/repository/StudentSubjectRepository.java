package com.studentdashboard.repository;

import com.studentdashboard.model.StudentSubject;
import com.studentdashboard.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    // JPQL: найти все курсы студента по семестру
    @Query("SELECT ss FROM StudentSubject ss JOIN FETCH ss.subject WHERE ss.student.id = :studentId AND ss.semester = :semester")
    List<StudentSubject> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") Integer semester);

    // JPQL: найти всех студентов, выбравших предмет в семестре
    @Query("SELECT ss.student FROM StudentSubject ss WHERE ss.subject.id = :subjectId AND ss.semester = :semester")
    List<Student> findStudentsBySubjectAndSemester(@Param("subjectId") Long subjectId, @Param("semester") Integer semester);

    // JPQL: количество курсов у студента в семестре
    @Query("SELECT COUNT(ss) FROM StudentSubject ss WHERE ss.student.id = :studentId AND ss.semester = :semester")
    int countByStudentAndSemester(@Param("studentId") Long studentId, @Param("semester") Integer semester);
}