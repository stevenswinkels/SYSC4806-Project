package com.example.demo.repository;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ApplicantRepositoryTest {

    @Autowired
    ApplicantRepository applicants;

    @Autowired
    ProfessorRepository professors;

    private Applicant applicant(String email, ApplicationStatus status) {
        Applicant a = new Applicant();
        a.setFullName("Test " + email);
        a.setEmail(email);
        a.setDesiredResearchField("Robotics");
        a.setStatus(status);
        return a;
    }

    @Test
    void findByEmailReturnsTheApplicant() {
        applicants.save(applicant("a@example.com", ApplicationStatus.SUBMITTED));

        assertTrue(applicants.findByEmail("a@example.com").isPresent());
        assertEquals("a@example.com", applicants.findByEmail("a@example.com").get().getEmail());
    }

    @Test
    void findByEmailUnknownIsEmpty() {
        assertTrue(applicants.findByEmail("nobody@example.com").isEmpty());
    }

    @Test
    void findByStatusReturnsOnlyMatchingApplicants() {
        applicants.save(applicant("a@example.com", ApplicationStatus.SUBMITTED));
        applicants.save(applicant("b@example.com", ApplicationStatus.DONT_RECOMMEND));
        applicants.save(applicant("c@example.com", ApplicationStatus.DONT_RECOMMEND));

        List<Applicant> found = applicants.findByStatus(ApplicationStatus.DONT_RECOMMEND);

        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(a -> a.getStatus() == ApplicationStatus.DONT_RECOMMEND));
    }

    @Test
    void findByAssignedProfessorReturnsOnlyThatProfessorsApplicants() {
        Professor euler = professors.save(new Professor("Dr. Euler", "euler@example.com",
                new HashSet<>(Set.of("Graph Theory"))));
        Professor turing = professors.save(new Professor("Dr. Turing", "turing@example.com",
                new HashSet<>(Set.of("Computability"))));

        Applicant mine = applicant("mine@example.com", ApplicationStatus.SUBMITTED);
        mine.assignTo(euler);
        Applicant other = applicant("other@example.com", ApplicationStatus.SUBMITTED);
        other.assignTo(turing);
        applicants.save(mine);
        applicants.save(other);

        List<Applicant> found = applicants.findByAssignedProfessor(euler);

        assertEquals(1, found.size());
        assertEquals("mine@example.com", found.get(0).getEmail());
    }
}