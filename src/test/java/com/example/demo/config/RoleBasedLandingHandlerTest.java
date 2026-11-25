package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleBasedLandingHandlerTest {

    private String landingFor(String role) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new RoleBasedLandingHandler().onAuthenticationSuccess(
                new MockHttpServletRequest(), response,
                new UsernamePasswordAuthenticationToken("user", null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))));
        return response.getRedirectedUrl();
    }

    @Test
    void adminLandsOnTheAdminDashboard() throws Exception {
        assertEquals("/admin", landingFor("ADMIN"));
    }

    @Test
    void professorLandsOnTheProfessorHomeRoute() throws Exception {
        assertEquals("/professor/applicants", landingFor("PROFESSOR"));
    }
}