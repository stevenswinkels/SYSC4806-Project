package com.example.demo.services;

import com.example.demo.model.Applicant;
import com.example.demo.model.Professor;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("professorAccess")
public class ProfessorAccess {

    private final UserAccountRepository accounts;
    private final ApplicantRepository applicants;

    public ProfessorAccess(UserAccountRepository accounts, ApplicantRepository applicants) {
        this.accounts = accounts;
        this.applicants = applicants;
    }

    public Optional<Professor> currentProfessor(Authentication auth) {
        if (auth == null) return Optional.empty();
        return accounts.findByEmail(auth.getName()).map(UserAccount::getProfessor);
    }

    public boolean isCurrentProfessor(Authentication auth, Long professorId) {
        return currentProfessor(auth)
                .map(p -> p.getId().equals(professorId))
                .orElse(false);
    }

    public boolean isAssignedToCurrent(Authentication auth, Long applicantId) {
        Optional<Professor> me = currentProfessor(auth);
        if (me.isEmpty()) return false;
        return applicants.findById(applicantId)
                .map(Applicant::getAssignedProfessor)
                .map(p -> p.getId().equals(me.get().getId()))
                .orElse(false);
    }
}