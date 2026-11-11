package com.example.demo.config;

import com.example.demo.model.Applicant;
import com.example.demo.model.Professor;
import com.example.demo.repository.ApplicantRepository;
import com.example.demo.repository.ProfessorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@Transactional
public class DataSeeder implements CommandLineRunner {
    private final ProfessorRepository professors;
    private final ApplicantRepository applicants;

    public DataSeeder(ProfessorRepository professors, ApplicantRepository applicants) { this.professors = professors;
        this.applicants = applicants;
    }

    @Override
    public void run(String... args) {
        if (professors.count() == 0) {
            professors.save(new Professor("Dr. Emmy Noether", "noether@example.com",
                    new HashSet<>(Set.of("Abstract Algebra", "Mathematical Physics"))));
            professors.save(new Professor("Dr. Alan Turing", "turing@example.com",
                    new HashSet<>(Set.of("Computability", "Machine Learning"))));
            professors.save(new Professor("Dr. Ada Lovelace", "lovelace@example.com",
                    new HashSet<>(Set.of("Algorithms", "Robotics"))));
            professors.save(new Professor("Dr. Leonhard Euler", "euler@example.com",
                    new HashSet<>(Set.of("Graph Theory", "Number Theory"))));
        }

        if (applicants.count() == 0) {
            Applicant applicant = new Applicant();
            applicant.setEmail("applicant@example.com");
            applicant.setFullName("Jimmy the Applicant");
            applicant.setDesiredResearchField("Mathematical Physics");
            applicant.setSubmittedAt(LocalDateTime.now());



            professors.findById(1L).ifPresent(professor -> {
                applicant.assignTo(professor);
                professors.save(professor);
                applicants.save(applicant);
            });
        }

        for (Professor p : professors.findAll()) {
            System.out.println("prof " + p.getName() + " with ID " + p.getId() + " was created and has " +
                    p.getAssignedApplicants().size() + " assigned applicants");
        }
    }
}

