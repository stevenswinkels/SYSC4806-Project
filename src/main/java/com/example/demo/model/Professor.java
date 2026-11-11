package com.example.demo.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Professor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> researchFields = new HashSet<>();

    @OneToMany(mappedBy = "assignedProfessor")
    private List<Applicant> assignedApplicants = new ArrayList<>();

    protected  Professor() {}

    public Professor(String name, String email, Set<String> researchFields){
        this.name = name;
        this.email = email;
        this.researchFields = researchFields;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<String> getResearchFields() { return researchFields; }
    public void setResearchFields(Set<String> f) { this.researchFields = f; }
    public List<Applicant> getAssignedApplicants(){ return assignedApplicants;}
}
