package com.example.demo.controller;

import com.example.demo.model.FinalDecision;
import com.example.demo.services.AdminDecisionService;
import com.example.demo.services.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminDecisionController {

    @Autowired
    private AdminDecisionService service;

    @GetMapping("/admin/applications")
    String list(Model model) {
        model.addAttribute("applicants", service.getApplicantsWithRecommendations());
        return "admin/applications";
    }

    @GetMapping("/admin/applications/{appId}")
    String detail(@PathVariable("appId") Long appId, Model model) {
        try {
            model.addAttribute("applicant", service.getApplicant(appId));
            model.addAttribute("decisions", FinalDecision.values());
            return "admin/application";
        } catch (NotFoundException e) {
            return "redirect:/admin/applications";
        }
    }

    @PostMapping("/admin/applications/{appId}/decision")
    String decide(@PathVariable("appId") Long appId,
                  @RequestParam("decision") String decision) {
        try {
            service.recordFinalDecision(appId, FinalDecision.valueOf(decision));
        } catch (NotFoundException | IllegalArgumentException e) {
            return "redirect:/admin/applications";
        }
        return "redirect:/admin/applications/" + appId;
    }
}