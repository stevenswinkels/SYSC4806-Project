package com.example.demo.services;

import com.example.demo.dto.ApplicantForm;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantFileRepository;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicantServiceTest {

    @Mock ApplicantRepository applicants;
    @Mock ProfessorRepository professors;
    @Mock DeadlineService deadlines;
    @Mock
    ApplicantFileRepository applicantFiles;
    @InjectMocks ApplicantService service;

    @BeforeEach
    void setUp() {
        when(deadlines.isApplicationOpen(any())).thenReturn(true);
    }

    private ApplicantForm bhagyaForm() {
        ApplicantForm f = new ApplicantForm();
        f.setFullName("Bhagya");
        f.setEmail("bhagya@example.com");
        f.setDesiredResearchField("Robotics");
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "cover-letter.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Cover letter content".getBytes()
        );
        f.setFiles(List.of(file));
        return f;
    }

    @Test
    void submitSavesWithSubmittedStatusTimestampAndProfessors() throws InvalidContentTypeException {
        Professor turing = new Professor("Dr. Alan Turing", "turing@example.com",
                new HashSet<>(Set.of("Computability")));
        ApplicantForm form = bhagyaForm();
        form.setProfessorIds(List.of(1L));

        when(applicants.findByEmail("bhagya@example.com")).thenReturn(Optional.empty());
        when(professors.findAllById(List.of(1L))).thenReturn(List.of(turing));
        when(applicants.save(any(Applicant.class))).thenAnswer(inv -> inv.getArgument(0));

        Applicant saved = service.submit(form);

        assertEquals(ApplicationStatus.SUBMITTED, saved.getStatus());
        assertNotNull(saved.getSubmittedAt());
        assertEquals(1, saved.getPreferredProfessors().size());
    }

    @Test
    void submitWithoutProfessorsStillSaves() throws InvalidContentTypeException {
        when(applicants.findByEmail("bhagya@example.com")).thenReturn(Optional.empty());
        when(applicants.save(any(Applicant.class))).thenAnswer(inv -> inv.getArgument(0));

        Applicant saved = service.submit(bhagyaForm());

        assertTrue(saved.getPreferredProfessors().isEmpty());
        verify(professors, never()).findAllById(any());
    }

    @Test
    void duplicateEmailIsRejectedAndNothingIsSaved() {
        when(applicants.findByEmail("bhagya@example.com"))
                .thenReturn(Optional.of(new Applicant()));

        assertThrows(DuplicateApplicationException.class,
                () -> service.submit(bhagyaForm()));
        verify(applicants, never()).save(any());
    }

    @Test
    void submitAfterDeadlineIsRejectedAndNothingIsSaved() {
        when(deadlines.isApplicationOpen(any())).thenReturn(false);

        assertThrows(DeadlinePassedException.class,
                () -> service.submit(bhagyaForm()));
        verify(applicants, never()).save(any());
    }

    @Test
    void rejectNonPdfFile() {
        ApplicantForm form = bhagyaForm();
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "cover-letter.pdf",
                MediaType.ALL_VALUE,
                "Cover letter content".getBytes()
        );
        List<MultipartFile> files = List.of(file);
        form.setFiles(files);
        assertThrows(InvalidContentTypeException.class, () -> service.submit(form));
    }
}