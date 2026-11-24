package com.example.demo.repository;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicantFile;
import com.example.demo.model.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ApplicantFileRepositoryTest {

    @Autowired
    private ApplicantFileRepository fileRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    private ApplicantFile applicantFile;

    private Applicant applicant;


    private Applicant createApplicant(String email, ApplicationStatus status) {
        Applicant a = new Applicant();
        a.setFullName("Test " + email);
        a.setEmail(email);
        a.setDesiredResearchField("Robotics");
        a.setStatus(status);
        return a;
    }

    @BeforeEach
    void setUp() {
        applicant = createApplicant("example@eexample.org", ApplicationStatus.SUBMITTED);
        applicantFile = new ApplicantFile();
        applicantFile.setFileName("name");
        applicantFile.setApplicant(applicant);
        applicantFile.setContents("abc".getBytes(StandardCharsets.UTF_8));
        applicantFile =  fileRepository.save(applicantFile);
        applicant = applicantRepository.save(applicant);
    }

    @Test
    void findById() {
        assertEquals(applicantFile, fileRepository.findById(applicantFile.getId()).get());
    }
}