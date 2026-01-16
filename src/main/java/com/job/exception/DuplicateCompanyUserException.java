package com.job.exception;

public class DuplicateCompanyUserException extends RuntimeException {
    public DuplicateCompanyUserException(String message) {
        super(message);
    }
}

