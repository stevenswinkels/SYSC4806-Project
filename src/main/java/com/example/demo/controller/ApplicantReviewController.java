package com.example.demo.controller;


import com.example.demo.dto.ApplicantDTO;
import com.example.demo.dto.ProfDTO;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.services.MailSenderService;
import com.example.demo.services.ApplicantReviewService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Transactional
public class ApplicantReviewController {

    @Autowired
    private ApplicantReviewService applicantReviewService;

    @GetMapping("/evaluations/{profID}/")
    String evaluations(@PathVariable("profID") Long profID){
        return "redirect:/evaluations/{profID}";
    }

    @GetMapping("/evaluations/{profID}")
    String evaluations(@PathVariable("profID") Long profID, Model model) {

        try{
        ProfDTO prof = applicantReviewService.getProf(profID);
            model.addAttribute("prof", prof);
            return "profs-applicants";
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
        ApplicantDTO applicant = applicantReviewService.getApplicantAssignedToProf(applicantID, profID);
        ProfDTO prof = applicantReviewService.getProf(profID);
        {
                    model.addAttribute("prof", prof);
                    model.addAttribute("applicant", applicant);
                    model.addAttribute("decisions", ApplicantReviewService.getVerdicts());
                    return "review-applicant";
                }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/evaluations/{profID}";



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
            applicantReviewService.assignVerdict(profID, applicantID, verdict);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }



        System.out.println("decision: " + decision);

        return "redirect:/evaluations/{profID}/";
    }
}
