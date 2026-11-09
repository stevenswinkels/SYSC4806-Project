package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Applicant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String fullName;
    @NotBlank @Email @Column(unique = true) private String email;
    @NotBlank private String desiredResearchField;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    private LocalDateTime submittedAt;

    @ManyToMany
    private List<Professor> preferedProfessor = new ArrayList<>();

    public Applicant() {};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDesiredResearchField() {
        return desiredResearchField;
    }

    public void setDesiredResearchField(String desiredResearchField) {
        this.desiredResearchField = desiredResearchField;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<Professor> getPreferedProfessor() {
        return preferedProfessor;
    }

    public void setPreferedProfessor(List<Professor> preferedProfessor) {
        this.preferedProfessor = preferedProfessor;
    }
}
