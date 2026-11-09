package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class ApplicantForm {

    @NotBlank private String fullName;
    @NotBlank @Email private String email;
    @NotBlank private String desiredResearchField;
    private List<Long> professorIds = new ArrayList<>();

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDesiredResearchField() { return desiredResearchField; }
    public void setDesiredResearchField(String f) { this.desiredResearchField = f; }
    public List<Long> getProfessorIds() { return professorIds; }
    public void setProfessorIds(List<Long> professorIds) { this.professorIds = professorIds; }
}