package com.job.exception;

public class CompanyUserNotFoundException extends RuntimeException {
    public CompanyUserNotFoundException(String message) {
        super(message);
    }
}

