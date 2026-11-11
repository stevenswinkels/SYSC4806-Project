package com.example.demo.repository;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    Optional<Applicant> findByEmail(String email);
    List<Applicant> findByStatus(ApplicationStatus status);
    List<Applicant> findByAssignedProfessor(Professor professor);
    List<Applicant> findByStatusIn(Collection<ApplicationStatus> statuses);
}