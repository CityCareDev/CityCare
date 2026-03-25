package com.citycare.dto.request;

import com.citycare.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStaffRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Provide a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private User.Role role;

    private String phone;

    // Optional: link to a facility (for DOCTOR/NURSE/DISPATCHER)
    private Long facilityId;
}
