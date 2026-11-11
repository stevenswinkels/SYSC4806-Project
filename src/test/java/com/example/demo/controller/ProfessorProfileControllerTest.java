package com.example.demo.controller;

import com.example.demo.dto.ProfDTO;
import com.example.demo.service.ProfessorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessorProfileController.class)
class ProfessorProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfessorService professorService;

    @Test
    void testShowEditProfileForm() throws Exception {
        ProfDTO profDTO = new ProfDTO();
        profDTO.setId(1L);
        profDTO.setName("Dr. Smith");
        profDTO.setEmail("smith@univ.edu");
        profDTO.setResearchFields(Set.of("Distributed Systems", "Cloud Computing"));

        when(professorService.getProfessorById(1L)).thenReturn(profDTO);

        mockMvc.perform(get("/professor/1/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/edit-profile"))
                .andExpect(model().attributeExists("professor"));
    }

    @Test
    void testSubmitProfileForm() throws Exception {
        mockMvc.perform(post("/professor/1/profile/edit")
                        .param("id", "1")
                        .param("name", "Dr. Smith")
                        .param("email", "smith@univ.edu")
                        .param("researchFields", "Network Security", "Cryptography"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/1/profile/edit?success"));

        verify(professorService).updateProfessorProfile(eq(1L), any(ProfDTO.class));
    }
}