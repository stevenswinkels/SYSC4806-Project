package com.example.demo.services;

public class DeadlinePassedException extends RuntimeException {
    public DeadlinePassedException(String message) {
        super(message);
    }
}