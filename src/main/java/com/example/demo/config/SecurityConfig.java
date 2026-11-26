package com.example.demo.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/apply", "/apply/**", "/login", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // admin-only; must stay ABOVE /professor/** because the first matching rule wins
                        .requestMatchers("/professor/create", "/professor/create/**").hasRole("ADMIN")
                        .requestMatchers("/professor/*/profile/edit").hasAnyRole("ADMIN", "PROFESSOR")
                        // /professors/** covers the applicant file download endpoint
                        .requestMatchers("/professor/**", "/professors/**", "/evaluations/**").hasRole("PROFESSOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(new RoleBasedLandingHandler())
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}