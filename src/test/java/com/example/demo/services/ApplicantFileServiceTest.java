package com.example.demo.services;

import com.example.demo.dto.ApplicantFileDTO;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicantFile;
import com.example.demo.repository.ApplicantFileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicantFileServiceTest {

    private ApplicantFileRepository applicantFileRepository;

    private ApplicantFileService fileService;

    private ApplicantFile applicantFile;


    private Applicant applicant;

    @BeforeEach
    void setUp() throws IOException {

        Applicant applicant = new Applicant();
        applicant.setId(1L);
        applicant.setEmail("test@example.org");
        applicant.setFullName("fullname");

        applicantFile = new ApplicantFile();
        applicantFile.setId(0L);
        applicantFile.setFileName("test.txt");
        applicantFile.setContents("test".getBytes(StandardCharsets.UTF_8));
        applicantFile.setApplicant(applicant);

        applicantFileRepository = mock(ApplicantFileRepository.class);

        when(applicantFileRepository.findById(applicantFile.getId())).thenReturn(Optional.of(applicantFile));
        when(applicantFileRepository.findById(1L)).thenReturn(Optional.empty());


        fileService = new ApplicantFileService(applicantFileRepository);
    }

    @Test
    void getApplicantFileById() throws NotFoundException {
        ApplicantFileDTO applicantFileDTO = new ApplicantFileDTO(applicantFile);
        Assertions.assertEquals(fileService.getApplicantFileById(0L), applicantFileDTO);
    }

    @Test
    void getApplicantNotFoundThrowsNotFoundException() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> fileService.getApplicantFileById(1L));
    }

    @Test
    void rejectNullApplicantId(){
        assertThrows(NotFoundException.class, () -> fileService.getApplicantFileById(null));
    }


}