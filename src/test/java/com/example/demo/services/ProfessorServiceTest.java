package com.example.demo.services;

import com.example.demo.services.ProfessorService;
import com.example.demo.dto.ProfDTO;
import com.example.demo.model.Professor;
import com.example.demo.repository.ProfessorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProfessorServiceTest {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ProfessorRepository professorRepository;

    @Test
    void testUpdateProfessorResearchFields() {
       Professor prof = new Professor("Dr. Jones", "jones@univ.edu", new HashSet<>(Set.of("Algorithms")));
        prof = professorRepository.save(prof);

        ProfDTO updateDto = new ProfDTO(prof);
        updateDto.setResearchFields(new HashSet<>(Set.of("Algorithms", "Machine Learning")));

        professorService.updateProfessorProfile(prof.getId(), updateDto);

        Professor updatedProf = professorRepository.findById(prof.getId()).orElseThrow();
        assertEquals(2, updatedProf.getResearchFields().size());
        assertTrue(updatedProf.getResearchFields().contains("Machine Learning"));
    }
}