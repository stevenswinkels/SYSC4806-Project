package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AuthModelAdvice {

    @ModelAttribute("signedIn")
    public boolean signedIn(Authentication auth) {
        return auth != null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication auth) {
        return hasRole(auth, "ROLE_ADMIN");
    }

    @ModelAttribute("isProfessor")
    public boolean isProfessor(Authentication auth) {
        return hasRole(auth, "ROLE_PROFESSOR");
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}