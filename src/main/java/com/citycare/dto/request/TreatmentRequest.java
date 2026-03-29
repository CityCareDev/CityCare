package com.citycare.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TreatmentRequest {

    @NotNull(message = "Patient ID is required")
    @Positive(message = "Patient ID must be a valid positive number")
    @Min(value = 1, message = "patient id  should be greater than 0")
    private Long patientId;

    @NotBlank(message = "Treatment description is required")
    private String description;

    private String medicationName; // Optional: e.g. "Amoxicillin"
    private String dosage;         // Optional: e.g. "500mg twice daily"
}

