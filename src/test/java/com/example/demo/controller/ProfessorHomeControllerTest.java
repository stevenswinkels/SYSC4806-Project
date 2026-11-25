package com.example.demo.controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.model.Professor;
import com.example.demo.services.ProfessorAccess;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessorHomeController.class)
@Import(SecurityConfig.class)
class ProfessorHomeControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ProfessorAccess access;

    @Test
    @WithMockUser(username = "turing@example.com", roles = "PROFESSOR")
    void professorIsSentToTheirOwnEvaluationsPage() throws Exception {
        Professor turing = new Professor("Dr. Alan Turing", "turing@example.com", new HashSet<>());
        ReflectionTestUtils.setField(turing, "id", 3L);
        when(access.currentProfessor(any())).thenReturn(Optional.of(turing));

        mvc.perform(get("/professor/applicants"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/evaluations/3"));
    }

    @Test
    @WithMockUser(roles = "PROFESSOR")
    void professorAccountWithoutAProfileIsForbidden() throws Exception {
        when(access.currentProfessor(any())).thenReturn(Optional.empty());

        mvc.perform(get("/professor/applicants")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotUseTheProfessorHomeRoute() throws Exception {
        mvc.perform(get("/professor/applicants")).andExpect(status().isForbidden());
    }

    @Test
    void anonymousUserIsSentToLogin() throws Exception {
        mvc.perform(get("/professor/applicants"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}