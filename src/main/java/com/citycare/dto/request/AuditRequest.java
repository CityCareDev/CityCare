package com.citycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuditRequest {
    @NotBlank(message = "Scope is required")
    private String scope;

    @PastOrPresent(message = "Audit date must be today or in the past")
    private LocalDate date;

    @Size(max = 1000, message = "Findings cannot exceed 1000 characters")
    private String findings;

}
