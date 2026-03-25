package com.citycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuditRequest {
    @NotBlank(message = "Scope is required")
    private String scope;

    private String findings;

    @NotNull(message = "Date is required")
    private LocalDate date;
}
