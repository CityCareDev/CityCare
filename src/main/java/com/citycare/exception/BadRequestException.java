package com.citycare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BadRequestException – Thrown for invalid business logic.
 * Example: "Ambulance TN-01-AB-1234 is not AVAILABLE (current: DISPATCHED)"
 * Example: "Emergency is already DISPATCHED – cannot dispatch again"
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}