package com.citycare.dto.request;

import com.citycare.entity.ComplianceRecord;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ComplianceRecordRequest {
    @NotNull
    private Long entityId;

    @NotNull
    private ComplianceRecord.EntityType type;

    @NotNull
    private ComplianceRecord.Result result;

    @PastOrPresent(message = "Compliance date must be today or in the past")
    private LocalDate date;


    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}

