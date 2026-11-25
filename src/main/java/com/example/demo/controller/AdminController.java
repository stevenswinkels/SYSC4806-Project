package com.example.demo.controller;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import com.example.demo.services.MailSenderService;
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

    @Autowired
    private MailSenderService mailSenderService;

    // list all applications, narrowed down by research field, preferred professor and status
    @GetMapping("/admin")
    String dashboard(@RequestParam(name = "field", required = false) String field,
                     @RequestParam(name = "preferred", required = false) Long preferred,
                     @RequestParam(name = "status", required = false) ApplicationStatus status,
                     Model model) {
        List<Applicant> applications = applicantRepository.findAll();
        if (field != null && !field.isBlank()) {
            applications = applications.stream()
                    .filter(a -> a.getDesiredResearchField().toLowerCase().contains(field.toLowerCase()))
                    .toList();
        }
        if (preferred != null) {
            applications = applications.stream()
                    .filter(a -> a.getPreferredProfessors().stream().anyMatch(p -> preferred.equals(p.getId())))
                    .toList();
        }
        if (status != null) {
            applications = applications.stream().filter(a -> a.getStatus() == status).toList();
        }
        model.addAttribute("applications", applications);
        model.addAttribute("professors", professorRepository.findAll());
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("selectedPreferred", preferred);
        model.addAttribute("selectedStatus", status);
        return "admin/dashboard";
    }

    // routes one application and emails the prof, only SUBMITTED applications can be routed
    private boolean routeApplication(Applicant applicant, Professor professor) {
        if (applicant.getStatus() != ApplicationStatus.SUBMITTED) {
            return false;
        }
        applicant.assignTo(professor);
        applicant.setStatus(ApplicationStatus.SENT_TO_PROFESSORS);
        applicantRepository.save(applicant);
        try {
            mailSenderService.sendNotificationOfAssignment(professor);
        } catch (Exception e) {
            // routing still counts if the email fails, e.g. no smtp server running locally
            System.out.println("could not email " + professor.getEmail() + ": " + e.getMessage());
        }
        return true;
    }

    // send the application to the chosen prof and email them about it
    @PostMapping("/admin/applications/{id}/route")
    String route(@PathVariable("id") Long id, @RequestParam("professorId") Long professorId) {
        Applicant applicant = applicantRepository.findById(id).orElse(null);
        Professor professor = professorRepository.findById(professorId).orElse(null);
        if (applicant != null && professor != null) {
            routeApplication(applicant, professor);
        }
        return "redirect:/admin";
    }
}
