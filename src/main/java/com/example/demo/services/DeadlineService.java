package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeadlineService {

    @Value("${custom.deadlines.application}")
    private LocalDateTime applicationDeadline;

    @Value("${custom.deadlines.evaluation}")
    private LocalDateTime evaluationDeadline;

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public LocalDateTime getEvaluationDeadline() {
        return evaluationDeadline;
    }

    // submitting exactly at the deadline is still allowed
    public boolean isApplicationOpen(LocalDateTime time) {
        return !time.isAfter(applicationDeadline);
    }

    public boolean isEvaluationOpen(LocalDateTime time) {
        return !time.isAfter(evaluationDeadline);
    }
}