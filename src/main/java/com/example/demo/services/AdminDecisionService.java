package com.example.demo.services;

import com.example.demo.model.Applicant;
import com.example.demo.model.FinalDecision;
import com.example.demo.repository.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminDecisionService {

    @Autowired
    private ApplicantRepository applicantRepository;

    public List<Applicant> getApplicantsWithRecommendations() {
        return applicantRepository.findByStatusIn(ApplicantReviewService.getVerdicts());
    }

    public Applicant getApplicant(Long appId) throws NotFoundException {
        return applicantRepository.findById(appId)
                .orElseThrow(() -> new NotFoundException("Applicant with ID " + appId + " not found"));
    }

    public void recordFinalDecision(Long appId, FinalDecision decision) throws NotFoundException {
        Applicant applicant = getApplicant(appId);
        applicant.setFinalDecision(decision);
        applicantRepository.save(applicant);
    }
}