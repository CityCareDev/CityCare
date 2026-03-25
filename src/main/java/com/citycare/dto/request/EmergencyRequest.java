package com.citycare.dto.request;

import com.citycare.entity.Emergency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * EmergencyRequest – Body for POST /emergencies/report
 * Sent by a logged-in CITIZEN to report an emergency.
 * CitizenId is NOT in this body – it's extracted from the JWT token.
 */
@Data
public class EmergencyRequest {

    @NotNull(message = "Emergency type is required")
    private Emergency.Type type;  // ACCIDENT, HEART_ATTACK, FIRE, STROKE, FALL, OTHER

    @NotBlank(message = "Location is required")
    private String location;

    private String description; // Optional extra details
}
