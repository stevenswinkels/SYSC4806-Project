package com.example.demo.services;

import com.example.demo.dto.ApplicantForm;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicantServiceTest {

    @Mock ApplicantRepository applicants;
    @Mock ProfessorRepository professors;
    @InjectMocks ApplicantService service;

    private ApplicantForm bhagyaForm() {
        ApplicantForm f = new ApplicantForm();
        f.setFullName("Bhagya");
        f.setEmail("bhagya@example.com");
        f.setDesiredResearchField("Robotics");
        return f;
    }

    @Test
    void submitSavesWithSubmittedStatusTimestampAndProfessors() {
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
    void submitWithoutProfessorsStillSaves() {
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
}