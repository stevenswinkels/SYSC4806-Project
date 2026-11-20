package com.example.demo.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeadlineServiceTest {

    LocalDateTime deadline = LocalDateTime.of(2026, 12, 1, 17, 0);
    DeadlineService service;

    @BeforeEach
    void setUp() {
        service = new DeadlineService();
        ReflectionTestUtils.setField(service, "applicationDeadline", deadline);
        ReflectionTestUtils.setField(service, "evaluationDeadline", deadline);
    }

    @Test
    void applicationOpenBeforeAndAtDeadlineButNotAfter() {
        assertTrue(service.isApplicationOpen(deadline.minusMinutes(1)));
        assertTrue(service.isApplicationOpen(deadline));
        assertFalse(service.isApplicationOpen(deadline.plusMinutes(1)));
    }

    @Test
    void evaluationOpenBeforeAndAtDeadlineButNotAfter() {
        assertTrue(service.isEvaluationOpen(deadline.minusMinutes(1)));
        assertTrue(service.isEvaluationOpen(deadline));
        assertFalse(service.isEvaluationOpen(deadline.plusMinutes(1)));
    }
}