package com.citycare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest – Body for POST /auth/login
 * Used by ALL roles: Citizen, Doctor, Nurse, Dispatcher, Admin.
 */
@Data
public class LoginRequest {

    @Email(message = "Provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}