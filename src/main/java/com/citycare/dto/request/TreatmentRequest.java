package com.citycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TreatmentRequest – Body for POST /treatments
 * Doctor or Nurse assigns a treatment to an admitted patient.
 * The assignedBy (staffId) is extracted from the JWT token – not in this body.
 */
@Data
public class TreatmentRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "Treatment description is required")
    private String description;

    private String medicationName; // Optional: e.g. "Amoxicillin"
    private String dosage;         // Optional: e.g. "500mg twice daily"
}
