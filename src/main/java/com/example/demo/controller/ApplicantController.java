package com.example.demo.controller;

import com.example.demo.dto.ApplicantForm;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicantFile;
import com.example.demo.services.ApplicantService;
import com.example.demo.services.DeadlinePassedException;
import com.example.demo.services.DuplicateApplicationException;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ApplicantController {

    private final ApplicantService service;

    public ApplicantController(ApplicantService service) {
        this.service = service;
    }

    @GetMapping("/apply")
    public String showForm(Model model) {
        model.addAttribute("form", new ApplicantForm());
        model.addAttribute("professors", service.getAllProfessors());
        model.addAttribute("deadline", service.getApplicationDeadline());
        return "apply";
    }

    @PostMapping("/apply")
    public String submit(@Valid @ModelAttribute("form") ApplicantForm form,
                         BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                Applicant saved = service.submit(form);
                return "redirect:/apply/confirmation/" + saved.getId();
            } catch (DuplicateApplicationException e) {
                result.rejectValue("email", "duplicate",
                        "An application with this email already exists");
            } catch (DeadlinePassedException e) {
                model.addAttribute("error", e.getMessage());
            } catch (InvalidContentTypeException e) {
                result.rejectValue("files", "notPDF","Only PDFs are accepted");
            }
        }
        model.addAttribute("professors", service.getAllProfessors());
        model.addAttribute("deadline", service.getApplicationDeadline());
        return "apply";
    }

    @GetMapping("/apply/confirmation/{id}")
    public String confirmation(@PathVariable Long id, Model model) {
        Applicant applicant = service.getApplicant(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        System.out.println("applicant has "  + applicant.getApplicantFiles().size() + " files");
        for(ApplicantFile f : applicant.getApplicantFiles()) {
            System.out.println(f);
        }
        model.addAttribute("applicant", applicant);
        return "confirmation";
    }
}