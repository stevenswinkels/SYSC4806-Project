package com.example.demo.services;


import com.example.demo.dto.ApplicantFileDTO;
import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicantFile;
import com.example.demo.repository.ApplicantFileRepository;
import com.example.demo.repository.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class ApplicantFileService {


    @Autowired
    ApplicantFileRepository applicantFileRepository;


    public ApplicantFileService() {}

    public ApplicantFileService(ApplicantFileRepository applicantFileRepository) {
        this.applicantFileRepository = applicantFileRepository;
    }

    /**
     * Retrieves applicant file data according to its id
     * @param id of the Applicant file
     * @return a DTO representing the file
     * @throws NotFoundException if the file is not found
     */
    public ApplicantFileDTO getApplicantFileById(Long id) throws NotFoundException {
        if (id == null) throw new NotFoundException("null id");
        Optional<ApplicantFile> applicantFile = applicantFileRepository.findById(id);
        if (applicantFile.isPresent()) {
            return new ApplicantFileDTO(applicantFile.get());
        }
        throw new NotFoundException("Applicant file with id " + id + " not found");
    }
}
