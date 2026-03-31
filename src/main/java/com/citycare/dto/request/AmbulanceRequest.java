package com.citycare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AmbulanceRequest {

    @NotBlank(message = "Vehicle number is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$",
            message = "Invalid vehicle number format (e.g., MH12AB1234)")
    private String vehicleNumber;

    @NotBlank(message = "Model name is required")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
    private String model;

}
