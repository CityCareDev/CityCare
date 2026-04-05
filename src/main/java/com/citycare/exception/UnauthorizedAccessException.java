package com.citycare.exception;


//Usage: This is crucial for your Hospital Administrator. If an Admin from "Hospital A" tries to edit a Staff
// member from "Hospital B," you throw this exception to block the action.


public class UnauthorizedAccessException extends RuntimeException{
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
