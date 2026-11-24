package com.example.demo.dto;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicantFile;

import java.util.Arrays;
import java.util.Objects;

public class ApplicantFileDTO {

    private Long id;
    private Applicant applicant;
    private byte[] data;
    private String fileName;

    public ApplicantFileDTO(ApplicantFile applicantFile) {
        this.id = (applicantFile.getId() == null)?null:applicantFile.getId();
        this.applicant = applicantFile.getApplicant();
        this.data = applicantFile.getContents();
        this.fileName = applicantFile.getFileName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicantFileDTO that = (ApplicantFileDTO) o;
        return Objects.equals(id, that.id) && Arrays.equals(data, that.data) && Objects.equals(fileName, that.fileName);
    }

    @Override
    public String toString() {
        return "ApplicantFileDTO{id=" + ((id==null)?"null":getId().toString()) + ", fileName=" + fileName;
    }
}
