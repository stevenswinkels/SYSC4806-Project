package com.example.demo.services;

import com.example.demo.dto.ApplicantForm;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicantService {

    private final ApplicantRepository applicants;
    private final ProfessorRepository professors;
    private final DeadlineService deadlines;

    public ApplicantService(ApplicantRepository applicants, ProfessorRepository professors, DeadlineService deadlines) {
        this.applicants = applicants;
        this.professors = professors;
        this.deadlines = deadlines;
    }

    public List<Professor> getAllProfessors() {
        return professors.findAll();
    }

    public Optional<Applicant> getApplicant(Long id) {
        return applicants.findById(id);
    }

    public Applicant submit(ApplicantForm form) {
        if (!deadlines.isApplicationOpen(LocalDateTime.now())) {
            throw new DeadlinePassedException("The application deadline has passed");
        }

        if (applicants.findByEmail(form.getEmail()).isPresent()) {
            throw new DuplicateApplicationException(form.getEmail());
        }

        Applicant applicant = new Applicant();
        applicant.setFullName(form.getFullName());
        applicant.setEmail(form.getEmail());
        applicant.setDesiredResearchField(form.getDesiredResearchField());
        applicant.setStatus(ApplicationStatus.SUBMITTED);
        applicant.setSubmittedAt(LocalDateTime.now());

        List<Long> ids = form.getProfessorIds();
        if (ids != null && !ids.isEmpty()) {
            applicant.setPreferredProfessors(new ArrayList<>(professors.findAllById(ids)));
        }
        return applicants.save(applicant);
    }
}