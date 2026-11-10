package com.example.demo.services;

import com.example.demo.dto.ProfDTO;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicantReviewServiceTest {
    @Mock
    ApplicantRepository applicants;
    @Mock
    ProfessorRepository professors;

    @InjectMocks
    ApplicantReviewService service;

    Professor professor;
    Applicant applicant;
    Applicant applicant2;

    @BeforeEach
    void setUp() {
        applicant = mock(Applicant.class);
        professor = mock(Professor.class);

        when(applicant.getId()).thenReturn(1L);
        when(applicant.getFullName()).thenReturn("name1");
        when(applicant.getEmail()).thenReturn("applicant@example.com");
        when(applicant.getAssignedProfessor()).thenReturn(professor);

        List<Applicant> applicantsList = new ArrayList<>();
        applicantsList.add(applicant);

        applicant2 = new Applicant();
        applicant2.setId(2L);


        when(professor.getId()).thenReturn(1L);
        when(professor.getAssignedApplicants()).thenReturn(applicantsList);


        when(applicants.findById(1L)).thenReturn(Optional.of(applicant));
        when(applicants.findById(2L)).thenReturn(Optional.of(applicant2));
        when(applicants.findById(3L)).thenReturn(Optional.empty());

        when(professors.findById(professor.getId())).thenReturn(Optional.of(professor));
        doReturn(Optional.empty()).when(professors).findById(2L);

    }


    @ParameterizedTest
    @EnumSource( value = ApplicationStatus.class, names = {"DONT_RECOMMEND", "RECOMMEND_NO_FUNDING",
    "RECOMMEND_NO_SUPERVISION", "RECOMMEND_WITH_FUNDING"})
    void correctVerdicts(ApplicationStatus status) {
        assertEquals(service.getVerdicts().size(), 4);
        assertTrue(service.getVerdicts().contains(status));
    }

    @Test
    void getProf() throws NotFoundException{
        assertEquals(service.getProf(1L).getId(), new ProfDTO(professor).getId());
        assertThrows(NotFoundException.class, () -> service.getProf(2L));
    }

    @Test
    void getApplicant() throws NotFoundException{
        assertThrows(NotFoundException.class, () -> service.getApplicantAssignedToProf(2L, 1L));
        assertThrows(NotFoundException.class, () -> service.getApplicantAssignedToProf(1L, 2L));
        assertThrows(NotFoundException.class, () -> service.getApplicantAssignedToProf(3L, 1L));
        assertEquals(1L, service.getApplicantAssignedToProf(1L, 1L).getId());
    }

    @ParameterizedTest
    @EnumSource( value = ApplicationStatus.class, names = {"DONT_RECOMMEND", "RECOMMEND_NO_FUNDING",
            "RECOMMEND_NO_SUPERVISION", "RECOMMEND_WITH_FUNDING"})
    void applyValidVerdict(ApplicationStatus status) {
        service.assignVerdict(professor.getId(), 1L, status);
        Mockito.verify(applicant).assignTo(null);
        Mockito.verify(applicants).save(applicant);
    }

    @ParameterizedTest
    @EnumSource( value = ApplicationStatus.class, names = {"DONT_RECOMMEND", "RECOMMEND_NO_FUNDING",
            "RECOMMEND_NO_SUPERVISION", "RECOMMEND_WITH_FUNDING"})
    void dontApplyVerdictIfApplicantNotAssignedToProf(ApplicationStatus status) {
        ApplicationStatus oldStatus = applicant2.getStatus();
        service.assignVerdict(professor.getId(), 2L, status);
        assertEquals(oldStatus, applicant2.getStatus());
        assertFalse(professor.getAssignedApplicants().isEmpty());
        Mockito.verify(professors, never()).save(professor);
        Mockito.verify(applicants, never()).save(applicant);
    }

    @ParameterizedTest
    @EnumSource( value = ApplicationStatus.class, names = {"DONT_RECOMMEND", "RECOMMEND_NO_FUNDING",
            "RECOMMEND_NO_SUPERVISION", "RECOMMEND_WITH_FUNDING"})
    void dontApplyVerdictIfApplicantNotFound(ApplicationStatus status) {
        ApplicationStatus oldStatus = applicant2.getStatus();
        service.assignVerdict(professor.getId(), 3L, status);
        assertEquals(oldStatus, applicant2.getStatus());
        assertFalse(professor.getAssignedApplicants().isEmpty());
        Mockito.verify(professors, never()).save(professor);
        Mockito.verify(applicants, never()).save(applicant);
    }


    @ParameterizedTest
    @EnumSource( value = ApplicationStatus.class, names = {"DONT_RECOMMEND", "RECOMMEND_NO_FUNDING",
            "RECOMMEND_NO_SUPERVISION", "RECOMMEND_WITH_FUNDING"})
    void dontApplyVerdictIfProfNotFound(ApplicationStatus status) {
        ApplicationStatus oldStatus = applicant2.getStatus();
        service.assignVerdict(2L, 2L, status);
        assertEquals(oldStatus, applicant2.getStatus());
        assertFalse(professor.getAssignedApplicants().isEmpty());
        Mockito.verify(professors, never()).save(professor);
        Mockito.verify(applicants, never()).save(applicant);
    }


}