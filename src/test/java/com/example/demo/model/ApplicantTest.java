package com.example.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApplicantTest {

    @Test
    void newApplicantDefaultsToSubmittedWithNoProfessors() {
        Applicant a = new Applicant();
        assertEquals(ApplicationStatus.SUBMITTED, a.getStatus());
        assertTrue(a.getPreferredProfessors().isEmpty());
    }

    @Test
    void settersUpdateFields() {
        Applicant a = new Applicant();
        a.setFullName("Bhagya");
        a.setEmail("bhagya@example.com");
        a.setDesiredResearchField("Robotics");
        a.setStatus(ApplicationStatus.UNDER_REVIEW);

        assertEquals("Bhagya", a.getFullName());
        assertEquals("bhagya@example.com", a.getEmail());
        assertEquals("Robotics", a.getDesiredResearchField());
        assertEquals(ApplicationStatus.UNDER_REVIEW, a.getStatus());
    }
}