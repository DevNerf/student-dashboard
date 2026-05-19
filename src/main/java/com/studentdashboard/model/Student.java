package com.studentdashboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    private String middleName;

    private String birthPlace;

    @Email(message = "Некорректный email")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Пароль обязателен")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    private String faculty;
    private String role = "STUDENT";

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();

    public Student() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getMiddleName() { return middleName; }           // ← ДОБАВЛЕНО
    public void setMiddleName(String middleName) { this.middleName = middleName; }  // ← ДОБАВЛЕНО
    public String getBirthPlace() { return birthPlace; }
    public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public GroupEntity getGroup() { return group; }
    public void setGroup(GroupEntity group) { this.group = group; }
    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }

    public String getFullName() {
        String result = lastName + " " + firstName;
        if (middleName != null && !middleName.isEmpty()) {
            result += " " + middleName;
        }
        return result;
    }
}