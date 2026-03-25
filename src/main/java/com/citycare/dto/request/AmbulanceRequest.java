package com.citycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AmbulanceRequest – Body for POST /admin/ambulances
 * Admin registers a new ambulance vehicle into the fleet.
 * It will start as AVAILABLE automatically.
 */
@Data
public class AmbulanceRequest {

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber; // e.g. "TN-01-AB-1234"

    private String model; // e.g. "Toyota HiAce ALS"
}
