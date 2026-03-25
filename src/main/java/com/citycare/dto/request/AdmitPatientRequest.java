package com.citycare.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * AdmitPatientRequest – Body for POST /patients/admit
 * Admin picks the emergencyId from GET /emergencies/dispatched
 * and provides it here to admit the citizen as a patient.
 * The system will automatically release the ambulance back to AVAILABLE.
 */
@Data
public class AdmitPatientRequest {

    @NotNull(message = "Emergency ID is required")
    private Long emergencyId;

    private String ward;  // e.g. "ICU", "General Ward", "Cardiology"
    private String notes; // Admission notes
}
