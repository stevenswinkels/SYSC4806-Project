package com.example.demo.config;

import com.example.demo.model.Professor;
import com.example.demo.model.Role;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.ProfessorRepository;
import com.example.demo.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Order(2)
public class AccountSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AccountSeeder.class);

    private final UserAccountRepository accounts;
    private final ProfessorRepository professors;
    private final PasswordEncoder encoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String professorPassword;

    public AccountSeeder(UserAccountRepository accounts, ProfessorRepository professors,
                         PasswordEncoder encoder,
                         @Value("${app.admin.email:admin@example.com}") String adminEmail,
                         @Value("${app.admin.password:}") String adminPassword,
                         @Value("${app.professor.password:}") String professorPassword) {
        this.accounts = accounts;
        this.professors = professors;
        this.encoder = encoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.professorPassword = professorPassword;
    }

    @Override
    public void run(String... args) {
        if (accounts.count() > 0) return;

        accounts.save(new UserAccount(adminEmail.toLowerCase(),
                encoder.encode(passwordFor("admin", adminPassword)), Role.ADMIN, null));

        String professorPw = passwordFor("professor", professorPassword);
        for (Professor p : professors.findAll()) {
            accounts.save(new UserAccount(p.getEmail().toLowerCase(),
                    encoder.encode(professorPw), Role.PROFESSOR, p));
        }
    }

    private String passwordFor(String who, String configured) {
        if (!configured.isBlank()) return configured;
        String generated = UUID.randomUUID().toString().substring(0, 12);
        log.warn("No password configured for {} accounts, generated one for this run: {}", who, generated);
        return generated;
    }
}