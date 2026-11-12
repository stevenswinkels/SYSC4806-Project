package com.example.demo.controller;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.FinalDecision;
import com.example.demo.services.AdminDecisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDecisionController.class)
class AdminDecisionControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    AdminDecisionService service;

    Applicant applicant;

    @BeforeEach
    void setup() throws Exception {
        applicant = new Applicant();
        applicant.setId(1L);
        applicant.setFullName("John Doe");
        applicant.setEmail("student@example.org");
        applicant.setDesiredResearchField("research field");
        applicant.setSubmittedAt(LocalDateTime.now());
        applicant.setStatus(ApplicationStatus.RECOMMEND_WITH_FUNDING);

        when(service.getApplicantsWithRecommendations()).thenReturn(List.of(applicant));
        when(service.getApplicant(1L)).thenReturn(applicant);
    }

    @Test
    void listPageShowsRecommendedApplicants() throws Exception {
        mvc.perform(get("/admin/applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/applications"))
                .andExpect(content().string(containsString("John Doe")));
    }

    @Test
    void detailPageShowsApplicant() throws Exception {
        mvc.perform(get("/admin/applications/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/application"))
                .andExpect(model().attributeExists("applicant"))
                .andExpect(content().string(containsString("John Doe")));
    }

    @Test
    void submittingDecisionRedirectsToDetailPage() throws Exception {
        mvc.perform(post("/admin/applications/1/decision").param("decision", "ADMIT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/applications/1"));

        verify(service).recordFinalDecision(1L, FinalDecision.ADMIT);
    }
}