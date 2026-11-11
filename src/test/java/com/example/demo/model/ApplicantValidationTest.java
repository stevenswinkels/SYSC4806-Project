package com.example.demo.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApplicantValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Applicant valid() {
        Applicant a = new Applicant();
        a.setFullName("Bhagya");
        a.setEmail("bhagya@example.com");
        a.setDesiredResearchField("Robotics");
        return a;
    }

    @Test
    void validApplicantHasNoViolations() {
        assertTrue(validator.validate(valid()).isEmpty());
    }

    @Test
    void blankNameIsRejected() {
        Applicant a = valid();
        a.setFullName("  ");
        assertFalse(validator.validate(a).isEmpty());
    }

    @Test
    void malformedEmailIsRejected() {
        Applicant a = valid();
        a.setEmail("not-an-email");
        assertFalse(validator.validate(a).isEmpty());
    }

    @Test
    void blankResearchFieldIsRejected() {
        Applicant a = valid();
        a.setDesiredResearchField("");
        assertFalse(validator.validate(a).isEmpty());
    }
}