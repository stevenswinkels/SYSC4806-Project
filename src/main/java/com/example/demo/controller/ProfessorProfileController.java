package com.example.demo.controller;

import com.example.demo.dto.ProfDTO;
import com.example.demo.services.ProfessorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/professor")
public class ProfessorProfileController {

    private final ProfessorService professorService;

    public ProfessorProfileController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    // Display form to edit professor profile
    @GetMapping("/{id}/profile/edit")
    public String showEditProfileForm(@PathVariable("id") Long id, Model model) {
        ProfDTO profDTO = professorService.getProfessorById(id);
        model.addAttribute("professor", profDTO);
        return "professor/edit-profile";
    }

    // Process profile submission
    @PostMapping("/{id}/profile/edit")
    public String updateProfile(@PathVariable("id") Long id, @ModelAttribute("professor") ProfDTO profDTO) {
        professorService.updateProfessorProfile(id, profDTO);
        return "redirect:/professor/" + id + "/profile/edit?success";
    }

    // Display creation form
    @GetMapping("/create")
    public String showCreateProfileForm(Model model) {
        model.addAttribute("professor", new ProfDTO());
        return "professor/create-profile";
    }

    // Process creation submission
    @PostMapping("/create")
    public String createProfile(@ModelAttribute("professor") ProfDTO profDTO) {
        ProfDTO createdProf = professorService.createProfessor(profDTO);
        return "redirect:/professor/" + createdProf.getId() + "/profile/edit?created";
    }
}