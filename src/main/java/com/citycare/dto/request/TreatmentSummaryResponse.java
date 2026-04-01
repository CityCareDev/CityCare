package com.citycare.dto.request;

import com.citycare.entity.Treatment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentSummaryResponse {
    private Long treatmentId;
    private Long patientId;
    private String patientName;

    // Patient table Details
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String ward;
    private String notes;
    private String patientStatus;

    // Staff Details
    private String assignedByDoctor;

    // Treatment table Details
    private String description;
    private String medicationName;
    private String dosage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Treatment.Status status;
}