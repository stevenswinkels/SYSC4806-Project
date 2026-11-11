package com.example.demo.services;

import com.example.demo.dto.ProfDTO;
import com.example.demo.model.Professor;
import com.example.demo.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public ProfDTO getProfessorById(Long id) {
        Professor prof = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid professor ID: " + id));
        return new ProfDTO(prof);
    }

    public void updateProfessorProfile(Long id, ProfDTO profDTO) {
        Professor prof = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid professor ID: " + id));

        prof.setName(profDTO.getName());
        prof.setEmail(profDTO.getEmail());
        prof.setResearchFields(profDTO.getResearchFields());

        professorRepository.save(prof);
    }

    public ProfDTO createProfessor(ProfDTO profDTO) {
        Professor prof = new Professor(
                profDTO.getName(),
                profDTO.getEmail(),
                profDTO.getResearchFields() != null ? new HashSet<>(profDTO.getResearchFields()) : new HashSet<>()
        );
        Professor saved = professorRepository.save(prof);
        return new ProfDTO(saved);
    }
}