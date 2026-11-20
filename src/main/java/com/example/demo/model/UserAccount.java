package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role;

    @OneToOne
    private Professor professor;   // set only for PROFESSOR accounts

    protected UserAccount() {}

    public UserAccount(String email, String passwordHash, Role role, Professor professor) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.professor = professor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}