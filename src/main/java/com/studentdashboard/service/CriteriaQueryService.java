package com.studentdashboard.service;

import com.studentdashboard.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CriteriaQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    //студенты, у которых нет курсов в семестре
    public List<Student> findStudentsWithoutElectives(int semester, Long groupId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> student = query.from(Student.class);

        // Подзапрос: студенты, у которых ЕСТЬ курсы в этом семестре
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<StudentSubject> ss = subquery.from(StudentSubject.class);
        subquery.select(ss.get("student").get("id"))
                .where(cb.equal(ss.get("semester"), semester));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.not(student.get("id").in(subquery)));

        if (groupId != null) {
            predicates.add(cb.equal(student.get("group").get("id"), groupId));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(student.get("lastName")));

        return entityManager.createQuery(query).getResultList();
    }

    //популярные предметы (по количеству записавшихся студентов)
    public List<Object[]> findPopularSubjects(int semester, int minStudents) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<StudentSubject> ss = query.from(StudentSubject.class);
        Join<StudentSubject, Subject> subject = ss.join("subject");

        query.multiselect(subject.get("name"), cb.count(ss.get("id")))
                .where(cb.equal(ss.get("semester"), semester))
                .groupBy(subject.get("name"))
                .having(cb.greaterThanOrEqualTo(cb.count(ss.get("id")), (long) minStudents))
                .orderBy(cb.desc(cb.count(ss.get("id"))));

        return entityManager.createQuery(query).getResultList();
    }
}