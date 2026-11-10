package com.example.demo.controller;


import com.example.demo.dto.ApplicantDTO;
import com.example.demo.dto.ProfDTO;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.services.ApplicantService;
import com.example.demo.services.MailSenderService;
import com.example.demo.services.ProfessorService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@Controller
@Transactional
public class ApplicantReviewController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private MailSenderService mailSenderService;

    @GetMapping("/evaluations/{profID}/")
    String evaluations(@PathVariable("profID") Long profID){
        return "redirect:/evaluations/{profID}";
    }

    @GetMapping("/evaluations/{profID}")
    String evaluations(@PathVariable("profID") Long profID, Model model) {

        try{
        ProfDTO prof = professorService.getProf(profID);
            model.addAttribute("prof", prof);
            return "evaluations";
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return "redirect:/";

    }



    @GetMapping("/evaluations/{profID}/{applicantID}")
    String evaluateApplication(@PathVariable("profID") Long profID,
                                @PathVariable("applicantID") Long applicantID,
                                Model model) {
        try {
        ApplicantDTO applicant = professorService.getApplicantAssignedToProf(applicantID, profID);
        ProfDTO prof = professorService.getProf(profID);
        {
                    model.addAttribute("prof", prof);
                    model.addAttribute("applicant", applicant);
                    model.addAttribute("decisions", professorService.getVerdicts());
                    return "applications";
                }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/evaluations/{profID}/";



    }

    @GetMapping("evaluations/{profID}/{applicantID}/")
    String postEvaluateApplicationsRedirect(@PathVariable("profID") Long profID,
                                    @PathVariable("applicantID") Long applicantID){
        return "redirect:/evaluations/{profID}/{applicantID}";
    }

    @PostMapping("evaluations/{profID}/{applicantID}")
    String postEvaluateApplications(@PathVariable("profID") Long profID,
                                    @PathVariable("applicantID") Long applicantID,
                                    @RequestParam(name = "decision", required = true) String decision){
        try {
            ApplicationStatus verdict = ApplicationStatus.valueOf(decision);
            professorService.assignVerdict(profID, applicantID, verdict);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }



        System.out.println("decision: " + decision);

        return "redirect:/evaluations/{profID}/";
    }
}
