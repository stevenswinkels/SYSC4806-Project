package com.example.demo.controller;

import com.example.demo.dto.ApplicantForm;
import com.example.demo.model.Applicant;
import com.example.demo.model.Professor;
import com.example.demo.services.ApplicantService;
import com.example.demo.services.DuplicateApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicantController.class)
class ApplicantControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ApplicantService service;

    @Test
    void getApplyShowsFormWithProfessors() throws Exception {
        Professor noether = new Professor("Dr. Emmy Noether", "noether@example.com",
                new HashSet<>(Set.of("Abstract Algebra")));
        ReflectionTestUtils.setField(noether, "id", 1L);
        when(service.getAllProfessors()).thenReturn(List.of(noether));

        mvc.perform(get("/apply"))
                .andExpect(status().isOk())
                .andExpect(view().name("apply"))
                .andExpect(model().attributeExists("form", "professors"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Dr. Emmy Noether")));
    }

    @Test
    void validPostRedirectsToConfirmation() throws Exception {
        Applicant saved = new Applicant();
        saved.setId(7L);
        when(service.submit(any(ApplicantForm.class))).thenReturn(saved);

        mvc.perform(post("/apply")
                        .param("fullName", "Bhagya")
                        .param("email", "bhagya@example.com")
                        .param("desiredResearchField", "Robotics"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apply/confirmation/7"));
    }

    @Test
    void invalidPostRedisplaysFormWithErrors() throws Exception {
        mvc.perform(post("/apply")
                        .param("fullName", "")
                        .param("email", "not-an-email")
                        .param("desiredResearchField", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("apply"))
                .andExpect(model().attributeHasFieldErrors("form",
                        "fullName", "email", "desiredResearchField"));
    }

    @Test
    void duplicateEmailShowsFieldError() throws Exception {
        when(service.submit(any(ApplicantForm.class)))
                .thenThrow(new DuplicateApplicationException("bhagya@example.com"));

        mvc.perform(post("/apply")
                        .param("fullName", "Bhagya")
                        .param("email", "bhagya@example.com")
                        .param("desiredResearchField", "Robotics"))
                .andExpect(status().isOk())
                .andExpect(view().name("apply"))
                .andExpect(model().attributeHasFieldErrors("form", "email"));
    }

    @Test
    void confirmationShowsApplicant() throws Exception {
        Applicant a = new Applicant();
        a.setId(7L);
        a.setFullName("Bhagya");
        a.setEmail("bhagya@example.com");
        a.setDesiredResearchField("Robotics");
        a.setSubmittedAt(LocalDateTime.of(2026, 11, 9, 10, 30));
        when(service.getApplicant(7L)).thenReturn(Optional.of(a));

        mvc.perform(get("/apply/confirmation/7"))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmation"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bhagya")));
    }

    @Test
    void confirmationForUnknownIdIs404() throws Exception {
        when(service.getApplicant(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/apply/confirmation/99"))
                .andExpect(status().isNotFound());
    }
}