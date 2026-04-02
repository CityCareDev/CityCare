package com.citycare.controller;

import com.citycare.dto.request.TreatmentRequest;
import com.citycare.dto.request.TreatmentSummaryResponse;
import com.citycare.dto.response.ApiResponse;
import com.citycare.entity.Treatment;

import com.citycare.exception.ResourceNotFoundException;
import com.citycare.repository.UserRepository;
import com.citycare.service.TreatmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treatments")
@RequiredArgsConstructor
@Tag(name = "4. Treatments", description = "Step 4: Doctor/Nurse assigns treatments and updates patient status.")
public class TreatmentController {

    private final TreatmentService treatmentService;
    private final UserRepository userRepository;

    // ── STEP 4: Assign treatment ──────────────────────────────────────────────

    @PostMapping
    @Operation(
            summary = "[DOCTOR/NURSE] Assign Treatment to Patient – Step 4",
            description = """
            POSTMAN: POST http://localhost:8080/api/treatments
            Header: Authorization: Bearer <DOCTOR_or_NURSE_TOKEN>
            Body:
            {
              "patientId": 1,
              "description": "Administer IV antibiotics every 8 hours for 5 days",
              "medicationName": "Amoxicillin",
              "dosage": "500mg IV every 8 hours"
            }
            → 'medicationName' and 'dosage' are optional.
            → assignedBy is automatically the logged-in doctor/nurse.
            → Treatment starts with status ONGOING.
            """
    )
    public ResponseEntity<ApiResponse<Treatment>> assign(
            @Valid @RequestBody TreatmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long staffId = resolveUserId(userDetails);
        Treatment treatment = treatmentService.assignTreatment(staffId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Treatment assigned", treatment));
    }

    // ── Get all treatments for a specific patient ─────────────────────────────

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "[DOCTOR/NURSE] Get All Treatments for a Patient",
            description = """
            POSTMAN: GET http://localhost:8080/api/treatments/patient/1
            Header: Authorization: Bearer <DOCTOR_or_NURSE_TOKEN>
            No body needed. Replace '1' with actual patientId.
            """
    )
    public ResponseEntity<ApiResponse<List<TreatmentSummaryResponse>>> getForPatient(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                ApiResponse.ok("Treatments for patient",
                        treatmentService.getForPatient(patientId)));
    }

    // ── Update treatment status ───────────────────────────────────────────────

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "[DOCTOR/NURSE] Update Treatment Status (Complete or Cancel)",
            description = """
            POSTMAN: PATCH http://localhost:8080/api/treatments/1/status?status=COMPLETED
            Header: Authorization: Bearer <DOCTOR_or_NURSE_TOKEN>
            No body needed. Status is a query parameter.
            
            Valid values: ONGOING | COMPLETED | CANCELLED
            
            Example URLs:
            PATCH /api/treatments/1/status?status=COMPLETED
            PATCH /api/treatments/2/status?status=CANCELLED
            
            On COMPLETED or CANCELLED → endDate is set to today automatically.
            """
    )
    public ResponseEntity<ApiResponse<Treatment>> updateStatus(
            @PathVariable Long id,
            @RequestParam Treatment.Status status) {

        Treatment treatment = treatmentService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Treatment status updated", treatment));
    }

    // ── Get all treatments assigned by the logged-in staff member ────────────

    @GetMapping("/mine")
    @Operation(
            summary = "[DOCTOR/NURSE] View Treatments I Assigned",
            description = """
            POSTMAN: GET http://localhost:8080/api/treatments/mine
            Header: Authorization: Bearer <DOCTOR_or_NURSE_TOKEN>
            No body needed.
            → Returns only treatments assigned by the logged-in doctor/nurse.
            """
    )
    public ResponseEntity<ApiResponse<List<TreatmentSummaryResponse>>> getMine(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long staffId = resolveUserId(userDetails);
        return ResponseEntity.ok(
                ApiResponse.ok("Your assigned treatments",
                        treatmentService.getMyAssigned(staffId)));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"))
                .getUserId();
    }
}

