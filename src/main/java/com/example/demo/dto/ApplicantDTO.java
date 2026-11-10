package com.example.demo.dto;

import com.example.demo.model.Applicant;
import com.example.demo.model.Professor;

import java.time.LocalDateTime;

public class ApplicantDTO {
    private Long id;
    private String FullName;
    private String Email;
    private LocalDateTime submittedAt;
    private Professor AssignedProf;
    private String desiredResearchField;
    public ApplicantDTO(Applicant applicant, Professor prof) {
        this.id = applicant.getId();
        this.FullName = applicant.getFullName();
        this.Email = applicant.getEmail();
        this.submittedAt = applicant.getSubmittedAt();
        this.desiredResearchField = applicant.getDesiredResearchField();
        this.AssignedProf = applicant.getAssignedProfessor();
    }

    public String getDesiredResearchField() {
        return desiredResearchField;
    }

    public void setDesiredResearchField(String desiredResearchField) {
        this.desiredResearchField = desiredResearchField;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Professor getAssignedProf() {
        return AssignedProf;
    }

    public void setAssignedProf(Professor assignedProf) {
        AssignedProf = assignedProf;
    }
}
