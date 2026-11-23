package com.example.demo.services;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.FinalDecision;
import com.example.demo.repository.ApplicantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDecisionServiceTest {

    @Mock
    ApplicantRepository applicants;

    @InjectMocks
    AdminDecisionService service;

    @Test
    void recordFinalDecisionSavesTheDecision() throws NotFoundException {
        Applicant applicant = new Applicant();
        when(applicants.findById(1L)).thenReturn(Optional.of(applicant));

        service.recordFinalDecision(1L, FinalDecision.ADMIT);

        assertEquals(FinalDecision.ADMIT, applicant.getFinalDecision());
        verify(applicants).save(applicant);

        assertEquals(ApplicationStatus.DECIDED, applicant.getStatus());
    }

    @Test
    void recordFinalDecisionForUnknownIdThrows() {
        when(applicants.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.recordFinalDecision(9L, FinalDecision.ADMIT));
    }

    @Test
    void listIncludesDecidedApplications(){
        Applicant decided = new Applicant();
        decided.setStatus(ApplicationStatus.DECIDED);

        when(applicants.findByStatusIn(argThat(s -> s.contains(ApplicationStatus.DECIDED))))
                .thenReturn(List.of(decided));

        assertEquals(List.of(decided), service.getApplicantsWithRecommendations());
    }
}