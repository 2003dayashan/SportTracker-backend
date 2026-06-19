package com.example.SportsTracker.exception;

public class DuplicateResourcesException extends RuntimeException {
    public DuplicateResourcesException(String message) {
        super(message);
    }
}