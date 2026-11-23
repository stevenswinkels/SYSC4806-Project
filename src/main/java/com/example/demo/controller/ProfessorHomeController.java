package com.example.demo.controller;

import com.example.demo.services.ProfessorAccess;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfessorHomeController {

    private final ProfessorAccess access;

    public ProfessorHomeController(ProfessorAccess access) {
        this.access = access;
    }

    @GetMapping("/professor/applicants")
    public String home(Authentication auth) {
        return access.currentProfessor(auth)
                .map(p -> "redirect:/evaluations/" + p.getId())
                .orElseThrow(() -> new AccessDeniedException("No professor profile for this account"));
    }
}