package com.example.demo.services;

import com.example.demo.dto.ApplicantDTO;
import com.example.demo.dto.ProfDTO;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ProfessorService {

    // Verdicts that a prof can give for an application
    private final Set<ApplicationStatus> verdicts = EnumSet.of(ApplicationStatus.DONT_RECOMMEND,
            ApplicationStatus.RECOMMEND_NO_FUNDING, ApplicationStatus.RECOMMEND_NO_SUPERVISION,
            ApplicationStatus.RECOMMEND_WITH_FUNDING);

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private ProfessorRepository professorRepository;

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
                return new ApplicantDTO(applicant, prof.get());
            }
        }

        throw new NotFoundException("Applicant with ID " + appId+ " not found in prof with ID " + profId + "'s " +
                "assigned applicants");

    }

    public Set<ApplicationStatus> getVerdicts() {
        return EnumSet.copyOf(verdicts);
    }

    /**
     *
     * @param profID the id of the prof attempting to assign the verdict
     * @param applicantID the id of the applicant to which the verdict is being applied
     * @param verdict the verdict being assigned
     */
    public void assignVerdict(Long profID, Long  applicantID, ApplicationStatus verdict){
        Optional<Applicant> applicantOpt = applicantRepository.findById(applicantID);
        if(applicantOpt.isPresent() && applicantOpt.get().getAssignedProfessor() != null){
            Applicant applicant = applicantOpt.get();

            if(verdicts.contains(verdict) && applicant.getAssignedProfessor().getId().equals(profID)) {
                applicant.setStatus(verdict);
                applicantRepository.save(applicant);
                // remove assignment
                applicant.assignTo(null);
            }

        }
    }
}
