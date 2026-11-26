package com.example.demo.controller;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import com.example.demo.services.MailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ApplicantRepository applicantRepository;

    @MockBean
    ProfessorRepository professorRepository;

    @MockBean
    MailSenderService mailSenderService;

    Professor professor;

    Applicant john;

    Applicant jane;

    @BeforeEach
    void setup() {
        professor = new Professor("prof name", "prof@example.org", new HashSet<>(Set.of("Robotics")));
        ReflectionTestUtils.setField(professor, "id", 1L);

        john = new Applicant();
        john.setId(1L);
        john.setFullName("John Doe");
        john.setEmail("john@example.org");
        john.setDesiredResearchField("Robotics");

        jane = new Applicant();
        jane.setId(2L);
        jane.setFullName("Jane Roe");
        jane.setEmail("jane@example.org");
        jane.setDesiredResearchField("Number Theory");

        when(applicantRepository.findAll()).thenReturn(List.of(john, jane));
        when(professorRepository.findAll()).thenReturn(List.of(professor));
    }

    @Test
    void dashboardListsApplications() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(content().string(containsString("John Doe")))
                .andExpect(content().string(containsString("Jane Roe")))
                .andExpect(content().string(containsString("prof name")))
                .andExpect(content().string(containsString("class=\"button\" href=\"/professor/create\"")))
                .andExpect(content().string(containsString("class=\"button\" href=\"/admin\"")))
                .andExpect(content().string(containsString(">Clear</a>")));
    }

    @Test
    void dashboardFiltersByResearchField() throws Exception {
        mvc.perform(get("/admin").param("field", "robot")).andExpect(status().isOk())
                .andExpect(content().string(containsString("John Doe")))
                .andExpect(content().string(not(containsString("Jane Roe"))));
    }

    @Test
    void routeAssignsProfessor() throws Exception {
        when(applicantRepository.findById(1L)).thenReturn(Optional.of(john));
        when(professorRepository.findById(Long.valueOf(1L))).thenReturn(Optional.of(professor));

        mvc.perform(post("/admin/applications/1/route").param("professorId", "1"))
                .andExpect(redirectedUrl("/admin"));

        assertEquals(professor, john.getAssignedProfessor());
        assertEquals(ApplicationStatus.SENT_TO_PROFESSORS, john.getStatus());
        verify(applicantRepository).save(john);
        verify(mailSenderService).sendNotificationOfAssignment(professor);
    }

    @Test
    void routeIgnoresDecidedApplication() throws Exception {
        john.setStatus(ApplicationStatus.DECIDED);
        when(applicantRepository.findById(1L)).thenReturn(Optional.of(john));
        when(professorRepository.findById(Long.valueOf(1L))).thenReturn(Optional.of(professor));

        mvc.perform(post("/admin/applications/1/route").param("professorId", "1"))
                .andExpect(redirectedUrl("/admin"));

        assertEquals(ApplicationStatus.DECIDED, john.getStatus());
        assertNull(john.getAssignedProfessor());
        verify(applicantRepository, never()).save(john);
        verify(mailSenderService, never()).sendNotificationOfAssignment(professor);
    }

    @Test
    void dashboardFiltersByPreferredProfessor() throws Exception {
        john.setPreferredProfessors(List.of(professor));

        mvc.perform(get("/admin").param("preferred", "1")).andExpect(status().isOk())
                .andExpect(content().string(containsString("John Doe")))
                .andExpect(content().string(not(containsString("Jane Roe"))));
    }

    @Test
    void dashboardFiltersByStatus() throws Exception {
        jane.setStatus(ApplicationStatus.DECIDED);

        mvc.perform(get("/admin").param("status", "DECIDED")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Jane Roe")))
                .andExpect(content().string(not(containsString("John Doe"))));
    }

    @Test
    void routeSelectedAssignsProfessorAndNotifiesForEach() throws Exception {
        when(applicantRepository.findById(1L)).thenReturn(Optional.of(john));
        when(applicantRepository.findById(2L)).thenReturn(Optional.of(jane));
        when(professorRepository.findById(Long.valueOf(1L))).thenReturn(Optional.of(professor));

        mvc.perform(post("/admin/applications/route")
                        .param("applicationIds", "1", "2")
                        .param("professorId", "1"))
                .andExpect(redirectedUrl("/admin"));

        assertEquals(professor, john.getAssignedProfessor());
        assertEquals(professor, jane.getAssignedProfessor());
        assertEquals(ApplicationStatus.SENT_TO_PROFESSORS, john.getStatus());
        assertEquals(ApplicationStatus.SENT_TO_PROFESSORS, jane.getStatus());
        verify(mailSenderService, times(2)).sendNotificationOfAssignment(professor);
    }

    @Test
    void routeSelectedSkipsDecidedAndUnknownApplications() throws Exception {
        john.setStatus(ApplicationStatus.DECIDED);
        when(applicantRepository.findById(1L)).thenReturn(Optional.of(john));
        when(applicantRepository.findById(99L)).thenReturn(Optional.empty());
        when(professorRepository.findById(Long.valueOf(1L))).thenReturn(Optional.of(professor));

        mvc.perform(post("/admin/applications/route")
                        .param("applicationIds", "1", "99")
                        .param("professorId", "1"))
                .andExpect(redirectedUrl("/admin"));

        assertNull(john.getAssignedProfessor());
        assertEquals(ApplicationStatus.DECIDED, john.getStatus());
        verify(applicantRepository, never()).save(any());
        verify(mailSenderService, never()).sendNotificationOfAssignment(any());
    }
}
