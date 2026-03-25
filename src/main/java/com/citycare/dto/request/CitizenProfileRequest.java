package com.citycare.dto.request;

import com.citycare.entity.Citizen;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CitizenProfileRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private LocalDate dateOfBirth;
    private Citizen.Gender gender;
    private String address;
    private String contactInfo;
}
