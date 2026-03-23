package com.citycare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceNotFoundException – Thrown when a DB record is not found.
 * Usage of @ResponseStatus(NOT_FOUND) → Spring returns HTTP 404 automatically.
 * Example: Emergency with id=99 not found → throw new ResourceNotFoundException("Emergency", 99L)
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
}