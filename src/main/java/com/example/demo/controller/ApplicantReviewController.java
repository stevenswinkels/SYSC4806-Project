package com.example.demo.controller;


import com.example.demo.dto.ApplicantDTO;
import com.example.demo.dto.ApplicantFileDTO;
import com.example.demo.dto.ProfDTO;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.services.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;

@Controller
@Transactional
public class ApplicantReviewController {

    @Autowired
    ApplicantFileService applicantFileService;

    @Autowired
    private ApplicantReviewService applicantReviewService;

    @GetMapping("/evaluations")
    String professorHome() {
        return "redirect:/professor/applicants";
    }

    @GetMapping("/evaluations/{profID}/")
    String evaluations(@PathVariable("profID") Long profID){
        return "redirect:/evaluations/{profID}";
    }

    @PreAuthorize("hasRole('PROFESSOR') and @professorAccess.isCurrentProfessor(authentication, #profID)")
    @GetMapping("/evaluations/{profID}")
    public String evaluations(@PathVariable("profID") Long profID, Model model) {

        try{
        ProfDTO prof = applicantReviewService.getProf(profID);
            model.addAttribute("prof", prof);
            return "applicant-review/profs-applicants";
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return "redirect:/";

    }



    @PreAuthorize("hasRole('PROFESSOR') and @professorAccess.isCurrentProfessor(authentication, #profID)"
            + " and @professorAccess.isAssignedToCurrent(authentication, #applicantID)")
    @GetMapping("/evaluations/{profID}/{applicantID}")
    public String evaluateApplication(@PathVariable("profID") Long profID,
                                      @PathVariable("applicantID") Long applicantID,
                                      Model model) {
        try {
        ApplicantDTO applicant = applicantReviewService.getApplicantAssignedToProf(applicantID, profID);
        ProfDTO prof = applicantReviewService.getProf(profID);
        {
                    model.addAttribute("prof", prof);
                    model.addAttribute("applicant", applicant);
                    model.addAttribute("decisions", ApplicantReviewService.getVerdicts());
                    model.addAttribute("deadline", applicantReviewService.getEvaluationDeadline());
                    return "applicant-review/review-applicant";
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

    @PreAuthorize("hasRole('PROFESSOR') and @professorAccess.isCurrentProfessor(authentication, #profID)"
            + " and @professorAccess.isAssignedToCurrent(authentication, #applicantID)")
    @PostMapping("evaluations/{profID}/{applicantID}")
    public String postEvaluateApplications(@PathVariable("profID") Long profID,
                                           @PathVariable("applicantID") Long applicantID,
                                           @RequestParam(name = "decision", required = true) String decision,
                                           RedirectAttributes redirectAttributes){
        try {
            ApplicationStatus verdict = ApplicationStatus.valueOf(decision);
            applicantReviewService.assignVerdict(profID, applicantID, verdict);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }catch (DeadlinePassedException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }



        System.out.println("decision: " + decision);

        return "redirect:/evaluations/{profID}";
    }

    @PreAuthorize("hasRole('PROFESSOR') and @professorAccess.isCurrentProfessor(authentication, #profId)"
            + " and @professorAccess.isAssignedToCurrent(authentication, #applicantId)")
    @GetMapping("/professors/{profId}/applicants/{applicantId}/files/{fileId}")
    public ResponseEntity<?> downloadApplicationFile(@PathVariable("profId") Long profId,
                                                     @PathVariable("applicantId") Long applicantId,
                                                     @PathVariable("fileId") Long fileId) {
        try {
            ApplicantFileDTO dto = applicantFileService.getFileForApplicant(fileId, applicantId);
            byte[] pdf = dto.getData();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + dto.getFileName() + "\"")
                    .contentLength(pdf.length)
                    .body(pdf);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
