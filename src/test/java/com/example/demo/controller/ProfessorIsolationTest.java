package com.example.demo.controller;

import com.example.demo.model.Applicant;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import jakarta.transaction.Transactional;
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
import com.example.demo.model.ApplicantFile;
import com.example.demo.repository.ApplicantFileRepository;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@AutoConfigureMockMvc
class ProfessorIsolationTest {

    @Autowired MockMvc mvc;
    @Autowired ProfessorRepository professors;
    @Autowired ApplicantRepository applicants;
    @Autowired ApplicantFileRepository files;

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

    /** Returns a file id for the applicant, creating a test file if the seeder made none. */
    private Long fileOf(Long applicantId) {
        Applicant applicant = applicants.findById(applicantId).orElseThrow();
        if (applicant.getApplicantFiles().isEmpty()) {
            applicant.addApplicantFile(newFile("test.pdf", applicant));
            applicants.saveAndFlush(applicant);
        }
        return applicant.getApplicantFiles().get(0).getId();
    }

    private ApplicantFile newFile(String name, Applicant owner) {
        ApplicantFile f = new ApplicantFile(name, "%PDF-1.4 test".getBytes());
        f.setApplicant(owner);
        return files.saveAndFlush(f);
    }

    private String downloadUrl(Long profId, Long applicantId, Long fileId) {
        return "/professors/" + profId + "/applicants/" + applicantId + "/files/" + fileId;
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

    @Test
    @Transactional
    @WithUserDetails("noether@example.com")
    void assignedProfessorCanDownloadTheirApplicantsFile() throws Exception {
        Long applicant = noethersApplicant();
        mvc.perform(get(downloadUrl(idOf("noether@example.com"), applicant, fileOf(applicant))))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithUserDetails("turing@example.com")
    void unassignedProfessorCannotDownloadAnotherProfessorsApplicantFile() throws Exception {
        Long applicant = noethersApplicant();
        Long file = fileOf(applicant);
        mvc.perform(get(downloadUrl(idOf("noether@example.com"), applicant, file)))
                .andExpect(status().isForbidden());
        mvc.perform(get(downloadUrl(idOf("turing@example.com"), applicant, file)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithUserDetails("noether@example.com")
    void nonexistentFileIsNotFound() throws Exception {
        Long applicant = noethersApplicant();
        mvc.perform(get(downloadUrl(idOf("noether@example.com"), applicant, 999999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithUserDetails("noether@example.com")
    void fileBelongingToAnotherApplicantIsNotFound() throws Exception {
        Long mine = noethersApplicant();

        Applicant other = new Applicant();
        other.setFullName("Other Applicant");
        other.setEmail("other-applicant@example.com");
        other.setDesiredResearchField("Testing");
        other = applicants.saveAndFlush(other);

        other.addApplicantFile(newFile("other.pdf", other));
        applicants.saveAndFlush(other);
        Long othersFileId = other.getApplicantFiles().get(0).getId();

        mvc.perform(get(downloadUrl(idOf("noether@example.com"), mine, othersFileId)))
                .andExpect(status().isNotFound());
    }
}