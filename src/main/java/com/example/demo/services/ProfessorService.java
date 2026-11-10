package com.example.demo.services;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Optional<Professor> getProf(Long id) {
        return professorRepository.findById(id);
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
        Optional<Professor> professor = professorRepository.findById(profID);
        Optional<Applicant> applicantOpt = applicantRepository.findById(applicantID);
        if(professor.isPresent() && applicantOpt.isPresent()){
            Applicant applicant = applicantOpt.get();
            Professor prof = professor.get();
        if(verdicts.contains(verdict) && prof.getAssignedApplicants().contains(applicant)) {
            applicant.setStatus(verdict);
            prof.getAssignedApplicants().remove(applicant);

            applicantRepository.save(applicant);
            professorRepository.save(prof);
        }

        }
    }
}
