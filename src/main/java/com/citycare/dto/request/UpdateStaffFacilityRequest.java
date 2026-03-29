package com.citycare.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStaffFacilityRequest {
    @NotNull
    private Long facilityId;
}
