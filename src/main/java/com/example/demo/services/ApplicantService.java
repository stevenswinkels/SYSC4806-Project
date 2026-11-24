package com.example.demo.services;

import com.example.demo.dto.ApplicantForm;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicantFile;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantFileRepository;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicantService {

    private final ApplicantRepository applicants;
    private final ProfessorRepository professors;
    private final DeadlineService deadlines;
    private final ApplicantFileRepository applicantFiles;

    public ApplicantService(ApplicantRepository applicants, ProfessorRepository professors, DeadlineService deadlines, ApplicantFileRepository applicantFiles) {
        this.applicants = applicants;
        this.professors = professors;
        this.deadlines = deadlines;
        this.applicantFiles = applicantFiles;
    }

    public List<Professor> getAllProfessors() {
        return professors.findAll();
    }

    public LocalDateTime getApplicationDeadline() {
        return deadlines.getApplicationDeadline();
    }
    public Optional<Applicant> getApplicant(Long id) {
        return applicants.findById(id);
    }

    public Applicant submit(ApplicantForm form) throws InvalidContentTypeException{
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
        //save uploaded files
        if (form.getFiles() != null && !form.getFiles().isEmpty()) {
            // only allow pdfs
            for(MultipartFile f : form.getFiles()) {
                String contentType = f.getContentType();
                if(contentType == null || !contentType.equals("application/pdf")) {
                    throw new InvalidContentTypeException("The file contains an invalid content type: " + contentType);
                }
            }
            //save files
            form.getFiles().forEach(file -> {
                try {
                    applicant.addApplicantFile(applicantFiles.save(new ApplicantFile(file.getOriginalFilename(), file.getBytes())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return applicants.save(applicant);
    }
}