package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class ApplicantFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    private String fileName;

    @Lob
    private byte[] contents;

    public ApplicantFile() {
    }
    public ApplicantFile(String fileName, byte[] contents) {
        this.fileName = fileName;
        this.contents = contents;
    }

    @ManyToOne
    @JoinColumn
    Applicant applicant;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] file) {
        this.contents = file;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }


}
