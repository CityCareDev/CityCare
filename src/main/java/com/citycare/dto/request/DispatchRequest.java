package com.citycare.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DispatchRequest – Body for POST /emergencies/{emergencyId}/dispatch
 * Dispatcher selects an available ambulance ID from
 * GET /emergencies/ambulances/available and provides it here.
 */
@Data
public class DispatchRequest {

    @NotNull(message = "Ambulance ID is required")
    private Long ambulanceId;
}
