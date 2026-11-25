package com.example.demo.services;

import com.example.demo.dto.ApplicantDTO;
import com.example.demo.dto.ApplicantFileDTO;
import com.example.demo.dto.ProfDTO;
import com.example.demo.model.*;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.EvaluationRepository;
import com.example.demo.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class ApplicantReviewService {

    // Verdicts that a prof can give for an application
    private static final Set<ApplicationStatus> verdicts = EnumSet.of(ApplicationStatus.DONT_RECOMMEND,
            ApplicationStatus.RECOMMEND_NO_FUNDING, ApplicationStatus.RECOMMEND_NO_SUPERVISION,
            ApplicationStatus.RECOMMEND_WITH_FUNDING);

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private ApplicantFileService applicantFileService;

    public ProfDTO getProf(Long id) throws NotFoundException {
        Optional<Professor> prof = professorRepository.findById(id);
        if (!prof.isPresent()) {
            throw new NotFoundException("professor with ID " + id + " not found");
        }
        return new ProfDTO(prof.get());
    }

    public ApplicantDTO getApplicantAssignedToProf(Long appId, Long profId) throws NotFoundException {
        Optional<Professor> prof = professorRepository.findById(profId);

        if (!prof.isPresent()) {
            throw new NotFoundException("professor with ID " + profId + " not found");
        }
        for (Applicant applicant : prof.get().getAssignedApplicants()) {
            if (applicant.getId().equals(appId)) {
                return new ApplicantDTO(applicant);
            }
        }

        throw new NotFoundException("Applicant with ID " + appId+ " not found in prof with ID " + profId + "'s " +
                "assigned applicants");

    }

    public LocalDateTime getEvaluationDeadline() {
        return deadlineService.getEvaluationDeadline();
    }

    public static Set<ApplicationStatus> getVerdicts() {
        return EnumSet.copyOf(verdicts);
    }

    /**
     *
     * @param profID the id of the prof attempting to assign the verdict
     * @param applicantID the id of the applicant to which the verdict is being applied
     * @param verdict the verdict being assigned
     */
    public void assignVerdict(Long profID, Long  applicantID, ApplicationStatus verdict){
        if (!deadlineService.isEvaluationOpen(LocalDateTime.now())) {
            throw new DeadlinePassedException("The evaluation deadline has passed");
        }
        Optional<Applicant> applicantOpt = applicantRepository.findById(applicantID);
        if(applicantOpt.isPresent() && applicantOpt.get().getAssignedProfessor() != null){
            Applicant applicant = applicantOpt.get();

            if(verdicts.contains(verdict) && applicant.getAssignedProfessor().getId().equals(profID)) {
                evaluationRepository.save(new Evaluation(applicant, applicant.getAssignedProfessor(), verdict, LocalDateTime.now()));
                applicant.setStatus(verdict);
                applicantRepository.save(applicant);
                // remove assignment
                applicant.assignTo(null);
            }

        }
    }
}
