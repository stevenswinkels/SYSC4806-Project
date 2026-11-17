package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Evaluation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Applicant applicant;

    @ManyToOne(optional = false)
    private Professor professor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus assessment;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    protected Evaluation() {}

    public Evaluation(Applicant applicant, Professor professor, ApplicationStatus assessment, LocalDateTime submittedAt) {
        this.applicant = applicant;
        this.professor = professor;
        this.assessment = assessment;
        this.submittedAt = submittedAt;
    }

    public Long getId() { return id; }
    public Applicant getApplicant() { return applicant; }
    public Professor getProfessor() { return professor; }
    public ApplicationStatus getAssessment() { return assessment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
}