package com.example.demo.services;

import com.example.demo.model.*;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessorAccessTest {

    @Mock UserAccountRepository accounts;
    @Mock ApplicantRepository applicants;
    @InjectMocks ProfessorAccess access;

    private Professor professor(long id) {
        Professor p = new Professor("Prof " + id, "p" + id + "@example.com", new HashSet<>());
        ReflectionTestUtils.setField(p, "id", id);
        return p;
    }

    private Authentication login(String email) {
        return new UsernamePasswordAuthenticationToken(email, null, List.of());
    }

    private void accountFor(Professor p) {
        when(accounts.findByEmail(p.getEmail()))
                .thenReturn(Optional.of(new UserAccount(p.getEmail(), "hash", Role.PROFESSOR, p)));
    }

    @Test
    void professorMatchesOnlyTheirOwnId() {
        Professor turing = professor(1);
        accountFor(turing);

        assertTrue(access.isCurrentProfessor(login(turing.getEmail()), 1L));
        assertFalse(access.isCurrentProfessor(login(turing.getEmail()), 2L));
    }

    @Test
    void adminAccountWithoutProfessorProfileMatchesNothing() {
        when(accounts.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(new UserAccount("admin@example.com", "hash", Role.ADMIN, null)));

        assertFalse(access.isCurrentProfessor(login("admin@example.com"), 1L));
        assertFalse(access.isAssignedToCurrent(login("admin@example.com"), 7L));
    }

    @Test
    void applicantIsOnlyOpenToTheAssignedProfessor() {
        Professor turing = professor(1);
        Professor euler = professor(2);
        Applicant a = new Applicant();
        a.assignTo(turing);
        accountFor(turing);
        accountFor(euler);
        when(applicants.findById(7L)).thenReturn(Optional.of(a));

        assertTrue(access.isAssignedToCurrent(login(turing.getEmail()), 7L));
        assertFalse(access.isAssignedToCurrent(login(euler.getEmail()), 7L));
    }

    @Test
    void unassignedOrUnknownApplicantIsClosedToEveryone() {
        Professor turing = professor(1);
        accountFor(turing);
        when(applicants.findById(7L)).thenReturn(Optional.of(new Applicant()));
        when(applicants.findById(99L)).thenReturn(Optional.empty());

        assertFalse(access.isAssignedToCurrent(login(turing.getEmail()), 7L));
        assertFalse(access.isAssignedToCurrent(login(turing.getEmail()), 99L));
    }
}