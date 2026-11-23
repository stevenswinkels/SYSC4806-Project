package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthModelAdviceTest {

    private final AuthModelAdvice advice = new AuthModelAdvice();

    private Authentication as(String role) {
        return new UsernamePasswordAuthenticationToken("user", null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    @Test
    void anonymousVisitorsAreNotSignedInAndHaveNoRoles() {
        assertFalse(advice.signedIn(null));
        assertFalse(advice.isAdmin(null));
        assertFalse(advice.isProfessor(null));
    }

    @Test
    void adminIsSignedInAndOnlyAdmin() {
        assertTrue(advice.signedIn(as("ADMIN")));
        assertTrue(advice.isAdmin(as("ADMIN")));
        assertFalse(advice.isProfessor(as("ADMIN")));
    }

    @Test
    void professorIsSignedInAndOnlyProfessor() {
        assertTrue(advice.signedIn(as("PROFESSOR")));
        assertTrue(advice.isProfessor(as("PROFESSOR")));
        assertFalse(advice.isAdmin(as("PROFESSOR")));
    }
}