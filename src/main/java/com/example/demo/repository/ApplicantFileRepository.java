package com.example.demo.repository;

import com.example.demo.model.ApplicantFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicantFileRepository extends JpaRepository<ApplicantFile, Long> {

    @Query("select f from Applicant a join a.applicantFiles f " +
            "where a.id = :applicantId and f.id = :fileId")
    Optional<ApplicantFile> findByIdAndApplicantId(@Param("fileId") Long fileId,
                                                   @Param("applicantId") Long applicantId);
}