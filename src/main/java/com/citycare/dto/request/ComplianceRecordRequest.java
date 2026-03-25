package com.citycare.dto.request;

import com.citycare.entity.ComplianceRecord;
import jakarta.validation.constraints.NotNull;
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

    private LocalDate date;
    private String notes;
}
