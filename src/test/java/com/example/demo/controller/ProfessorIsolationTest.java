package com.example.demo.controller;

import com.example.demo.model.Applicant;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfessorIsolationTest {

    @Autowired MockMvc mvc;
    @Autowired ProfessorRepository professors;
    @Autowired ApplicantRepository applicants;

    private Long idOf(String email) {
        return professors.findAll().stream()
                .filter(p -> p.getEmail().equals(email))
                .map(Professor::getId)
                .findFirst().orElseThrow();
    }

    /** An applicant assigned to Noether (the seeder assigns one). */
    private Long noethersApplicant() {
        Long noether = idOf("noether@example.com");
        return applicants.findAll().stream()
                .filter(a -> a.getAssignedProfessor() != null
                        && a.getAssignedProfessor().getId().equals(noether))
                .map(Applicant::getId)
                .findFirst().orElseThrow();
    }

    @Test
    @WithUserDetails("turing@example.com")
    void professorOpensTheirOwnApplicantList() throws Exception {
        mvc.perform(get("/evaluations/" + idOf("turing@example.com")))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("turing@example.com")
    void professorCannotOpenAnotherProfessorsApplicantList() throws Exception {
        mvc.perform(get("/evaluations/" + idOf("noether@example.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("turing@example.com")
    void professorCannotOpenAnotherProfessorsApplicantPage() throws Exception {
        Long theirs = noethersApplicant();
        mvc.perform(get("/evaluations/" + idOf("noether@example.com") + "/" + theirs))
                .andExpect(status().isForbidden());
        mvc.perform(get("/evaluations/" + idOf("turing@example.com") + "/" + theirs))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("turing@example.com")
    void professorCannotSubmitAVerdictForAnotherProfessorsApplicant() throws Exception {
        Long theirs = noethersApplicant();
        mvc.perform(post("/evaluations/" + idOf("noether@example.com") + "/" + theirs)
                        .param("decision", "RECOMMEND_WITH_FUNDING").with(csrf()))
                .andExpect(status().isForbidden());
        mvc.perform(post("/evaluations/" + idOf("turing@example.com") + "/" + theirs)
                        .param("decision", "RECOMMEND_WITH_FUNDING").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("turing@example.com")
    void professorCanEditOnlyTheirOwnProfile() throws Exception {
        mvc.perform(get("/professor/" + idOf("turing@example.com") + "/profile/edit"))
                .andExpect(status().isOk());
        mvc.perform(get("/professor/" + idOf("noether@example.com") + "/profile/edit"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/professor/" + idOf("noether@example.com") + "/profile/edit").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("turing@example.com")
    void professorCannotCreateProfessorProfiles() throws Exception {
        mvc.perform(get("/professor/create")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin@example.com")
    void adminCanCreateAndEditProfessorProfiles() throws Exception {
        mvc.perform(get("/professor/create")).andExpect(status().isOk());
        mvc.perform(get("/professor/" + idOf("turing@example.com") + "/profile/edit"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin@example.com")
    void adminCannotUseTheProfessorReviewPages() throws Exception {
        mvc.perform(get("/evaluations/" + idOf("noether@example.com")))
                .andExpect(status().isForbidden());
    }
}