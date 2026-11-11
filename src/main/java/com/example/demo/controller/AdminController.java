package com.example.demo.controller;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    // list all applications, or only the ones whose research field contains the filter
    @GetMapping("/admin")
    String dashboard(@RequestParam(name = "field", required = false) String field, Model model) {
        List<Applicant> applications = applicantRepository.findAll();
        if (field != null && !field.isBlank()) {
            applications = applications.stream()
                    .filter(a -> a.getDesiredResearchField().toLowerCase().contains(field.toLowerCase()))
                    .toList();
        }
        model.addAttribute("applications", applications);
        model.addAttribute("professors", professorRepository.findAll());
        return "admin/dashboard";
    }

    // send the application to the chosen prof
    @PostMapping("/admin/applications/{id}/route")
    String route(@PathVariable("id") Long id, @RequestParam("professorId") Long professorId) {
        Applicant applicant = applicantRepository.findById(id).orElse(null);
        Professor professor = professorRepository.findById(professorId).orElse(null);
        if (applicant != null && professor != null) {
            applicant.assignTo(professor);
            applicant.setStatus(ApplicationStatus.SENT_TO_PROFESSORS);
            applicantRepository.save(applicant);
        }
        return "redirect:/admin";
    }
}
