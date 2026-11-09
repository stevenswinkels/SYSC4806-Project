package com.example.demo.config;

import com.example.demo.model.Professor;
import com.example.demo.repository.ProfessorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProfessorRepository professors;
    public DataSeeder(ProfessorRepository professors) { this.professors = professors; }

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
    }
}

