package com.example.demo.model;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EntityPersistenceTest {

    @Autowired
    private TestEntityManager em;

    private Applicant bhagya(String email) {
        Applicant a = new Applicant();
        a.setFullName("Bhagya");
        a.setEmail(email);
        a.setDesiredResearchField("Robotics");
        return a;
    }

    private Professor noether() {
        return new Professor("Dr. Emmy Noether", "noether@example.com",
                new HashSet<>(Set.of("Abstract Algebra", "Mathematical Physics")));
    }

    @Test
    void professorResearchFieldsArePersisted() {
        Professor p = noether();
        em.persistAndFlush(p);
        em.clear();

        Professor found = em.find(Professor.class, p.getId());
        assertEquals(2, found.getResearchFields().size());
        assertTrue(found.getResearchFields().contains("Abstract Algebra"));
    }

    @Test
    void applicantKeepsPreferredProfessors() {
        Professor turing = em.persist(new Professor("Dr. Alan Turing", "turing@example.com",
                new HashSet<>(Set.of("Computability", "Machine Learning"))));
        Applicant a = bhagya("bhagya@example.com");
        a.getPreferredProfessors().add(turing);
        em.persistAndFlush(a);
        em.clear();

        Applicant found = em.find(Applicant.class, a.getId());
        assertEquals(1, found.getPreferredProfessors().size());
        assertEquals("Dr. Alan Turing", found.getPreferredProfessors().get(0).getName());
    }

    @Test
    void statusEnumRoundTrips() {
        Applicant a = bhagya("bhagya@example.com");
        a.setStatus(ApplicationStatus.UNDER_REVIEW);
        em.persistAndFlush(a);
        em.clear();

        assertEquals(ApplicationStatus.UNDER_REVIEW,
                em.find(Applicant.class, a.getId()).getStatus());
    }

    @Test
    void duplicateApplicantEmailIsRejected() {
        em.persistAndFlush(bhagya("same@example.com"));
        assertThrows(PersistenceException.class,
                () -> em.persistAndFlush(bhagya("same@example.com")));
    }
}