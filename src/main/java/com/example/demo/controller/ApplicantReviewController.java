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

import java.net.URI;

@Controller
@Transactional
public class ApplicantReviewController {

    @Autowired
    ApplicantFileService applicantFileService;

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
            return "applicant-review/profs-applicants";
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

    @PostMapping("evaluations/{profID}/{applicantID}")
    String postEvaluateApplications(@PathVariable("profID") Long profID,
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

    @GetMapping("professors/{id1}/applicants/{id2}/files/{id3}")
    ResponseEntity<?> downloadApplicationFile(@PathVariable("id1") Long profId, @PathVariable("id2") Long applicantId, @PathVariable("id3") Long fileId, Model model){
        try{
            ApplicantFileDTO dto = applicantFileService.getApplicantFileById(fileId);
            byte[] pdf = dto.getData();
            ResponseEntity<?> response = ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; "+ "filename=\"" + dto.getFileName() +"\""
                    )
                    .contentLength(pdf.length)
                    .body(pdf);
            return response;
        } catch (NotFoundException e) {
            String uri = "redirect:/evaluations/" + Long.toString(profId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .location(URI.create(uri))
                    .build();
        }

    }
}
