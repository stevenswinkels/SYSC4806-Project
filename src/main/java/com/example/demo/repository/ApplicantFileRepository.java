package com.example.demo.repository;

import com.example.demo.model.Applicant;
import com.example.demo.model.ApplicantFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicantFileRepository extends JpaRepository<ApplicantFile, Long> {
}
