package com.example.demo.repository;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Evaluation;
import com.example.demo.model.Professor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EvaluationRepositoryTest {

    @Autowired
    EvaluationRepository evaluations;

    @Autowired
    ApplicantRepository applicants;

    @Autowired
    ProfessorRepository professors;

    private Applicant applicant(String email) {
        Applicant a = new Applicant();
        a.setFullName("Test " + email);
        a.setEmail(email);
        a.setDesiredResearchField("Robotics");
        return applicants.save(a);
    }

    @Test
    void findByApplicantReturnsOnlyThatApplicantsEvaluations() {
        Professor euler = professors.save(new Professor("Dr. Euler", "euler@example.com",
                new HashSet<>(Set.of("Graph Theory"))));
        Applicant mine = applicant("mine@example.com");
        Applicant other = applicant("other@example.com");

        evaluations.save(new Evaluation(mine, euler, ApplicationStatus.RECOMMEND_NO_FUNDING, LocalDateTime.now()));
        evaluations.save(new Evaluation(other, euler, ApplicationStatus.DONT_RECOMMEND, LocalDateTime.now()));

        List<Evaluation> found = evaluations.findByApplicantOrderBySubmittedAtDesc(mine);

        assertEquals(1, found.size());
        assertEquals(ApplicationStatus.RECOMMEND_NO_FUNDING, found.get(0).getAssessment());
        assertEquals("Dr. Euler", found.get(0).getProfessor().getName());
    }

    @Test
    void findByApplicantReturnsNewestFirst() {
        Professor euler = professors.save(new Professor("Dr. Euler", "euler@example.com",
                new HashSet<>(Set.of("Graph Theory"))));
        Applicant mine = applicant("mine@example.com");
        LocalDateTime now = LocalDateTime.now();

        evaluations.save(new Evaluation(mine, euler, ApplicationStatus.DONT_RECOMMEND, now.minusDays(1)));
        evaluations.save(new Evaluation(mine, euler, ApplicationStatus.RECOMMEND_WITH_FUNDING, now));

        List<Evaluation> found = evaluations.findByApplicantOrderBySubmittedAtDesc(mine);

        assertEquals(ApplicationStatus.RECOMMEND_WITH_FUNDING, found.get(0).getAssessment());
        assertEquals(ApplicationStatus.DONT_RECOMMEND, found.get(1).getAssessment());
    }
}