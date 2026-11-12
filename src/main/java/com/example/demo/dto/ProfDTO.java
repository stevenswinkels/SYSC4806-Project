package com.example.demo.dto;

import com.example.demo.model.Applicant;
import com.example.demo.model.Professor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProfDTO {
    private List<Applicant> applicants;
    private String name;
    private String email;
    private Long  id;
    private Professor professor;
    private Set<String> researchFields;

    // Required default constructor for Spring MVC    
    public ProfDTO() {}

    public ProfDTO(Long id, String name, String email, String researchFields) {
        this.id = id;
        this.name = name;
        this.email = email;
        setResearchFields(researchFields == null || researchFields.isBlank()
                ? Set.of()
                : new LinkedHashSet<>(Arrays.asList(researchFields.split("\\s*,\\s*"))));
    }

    public ProfDTO(Professor prof) {
        if (prof != null) {
            this.id = prof.getId();
            this.name = prof.getName();
            this.email = prof.getEmail();
            this.applicants = prof.getAssignedApplicants();
            this.researchFields = prof.getResearchFields() != null ? new HashSet<>(prof.getResearchFields()) : new HashSet<>();
        }
    }

    public List<Applicant> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<Applicant> applicants) {
        this.applicants = applicants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Set<String> getResearchFields() {
        return researchFields;
    }

    public void setResearchFields(Set<String> researchFields) {
        this.researchFields = researchFields;
    }
}
