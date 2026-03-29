package com.citycare.dto.request;

import com.citycare.entity.Facility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FacilityRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Type is required")
    private Facility.Type type;

    @NotBlank(message = "Location is required")
    private String location;

    private int capacity;

    private Facility.Status status;
}
