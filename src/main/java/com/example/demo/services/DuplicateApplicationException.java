package com.example.demo.services;

public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(String email) {
        super("An application already exists for " + email);
    }
}