package com.example.demo.controller;

import com.example.demo.dto.ApplicantDTO;
import com.example.demo.dto.ProfDTO;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.services.ApplicantReviewService;

import com.example.demo.services.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ApplicantReviewController.class)
class ApplicantReviewControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ApplicantReviewService service;


    Professor professor;

    Applicant applicant;

    @BeforeEach
    void setup() throws Exception {
        professor = new Professor("prof name", "prof@example.org", new HashSet<>());
        ReflectionTestUtils.setField(professor, "id", 1L);

        applicant = new Applicant();
        applicant.setId(1L);
        applicant.setFullName("John Doe");
        applicant.setEmail("student@example.org");
        applicant.setDesiredResearchField("research field");
        applicant.setSubmittedAt(LocalDateTime.now());
        applicant.assignTo(professor);

        when(service.getProf(1L)).thenReturn(new ProfDTO(professor));
        when(service.getProf(2L)).thenThrow(new NotFoundException("prof not found"));

        when(service.getApplicantAssignedToProf(1L, 1L)).thenReturn(new ApplicantDTO(applicant));
        when(service.getApplicantAssignedToProf(2L, 1L)).thenThrow(new NotFoundException("applicant not found"));
        when(service.getApplicantAssignedToProf(1L, 2L)).thenThrow(new NotFoundException("prof not found"));
    }

    @Test
    void evaluationsPageValidProfId() throws Exception {
        mvc.perform(get("/evaluations/1")).andExpect(status().isOk())
                .andExpect(view().name("applicant-review/profs-applicants"))
                .andExpect(model().attributeExists("prof"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("prof name")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("John Doe")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("View Application")));
    }

    @Test
    void evaluationsPageRedirect() throws Exception {
        mvc.perform(get("/evaluations/1/")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/evaluations/1"));
    }

    @Test
    void evaluationsPageInvalidProfId() throws Exception {
        mvc.perform(get("/evaluations/2")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void applicantReviewPageValidProfIdValidAppId() throws Exception {
        mvc.perform(get("/evaluations/1/1")).andExpect(status().isOk())
                .andExpect(view().name("applicant-review/review-applicant"))
                .andExpect(model().attributeExists("applicant"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("John Doe")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("student@example.org")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("research field")));
    }

    @Test
    void applicantReviewPageValidProfIdInvalidAppId() throws Exception {
        mvc.perform(get("/evaluations/1/2")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/evaluations/1"));
    }

    @Test
    void applicantReviewPageValidProfIdValidAppIdRedirect() throws Exception {
        mvc.perform(get("/evaluations/1/1/")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/evaluations/1/1"));
    }

    @Test
    void submitReviewRedirects() throws Exception {
        mvc.perform(post("/evaluations/1/1").param("decision", ApplicationStatus.DONT_RECOMMEND.toString())).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/evaluations/1"));
    }

    @Test
    void submitReviewCallsService() throws Exception {
        mvc.perform(post("/evaluations/1/1").param("decision", "DONT_RECOMMEND"))
                .andExpect(status().is3xxRedirection());

        org.mockito.Mockito.verify(service).assignVerdict(1L, 1L, ApplicationStatus.DONT_RECOMMEND);
    }
}