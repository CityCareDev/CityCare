package com.citycare.controller;


import com.citycare.dto.request.AdmitPatientRequest;
import com.citycare.dto.response.ApiResponse;
import com.citycare.entity.Patient;
import com.citycare.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "3. Patients", description = "Step 3: Admin admits patient (auto-releases ambulance). Staff updates status.")
public class PatientController {

    private final PatientService patientService;

    // ── STEP 3: Admin admits patient ──────────────────────────────────────────

    @PostMapping("/admit")
    @Operation(
            summary = "[ADMIN] Admit Patient from Dispatched Emergency – Step 3",
            description = """
            PREREQUISITE: Emergency must be DISPATCHED.
            Get emergencyId from: GET /emergencies/dispatched
            
            POSTMAN: POST http://localhost:8080/api/patients/admit
            Header: Authorization: Bearer <ADMIN_TOKEN>
            Body:
            {
              "emergencyId": 1,
              "ward": "ICU",
              "notes": "Patient arrived conscious. BP 140/90."
            }
            
            AUTO-HAPPENS:
            → Patient created (status: ADMITTED)
            → Emergency: DISPATCHED → ADMITTED
            → Ambulance: DISPATCHED → AVAILABLE (released automatically!)
            """
    )
    public ResponseEntity<ApiResponse<Patient>> admit(
            @Valid @RequestBody AdmitPatientRequest request) {

        Patient patient = patientService.admitPatient(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Patient admitted successfully. Ambulance released.", patient));
    }

    // ── Get all patients ──────────────────────────────────────────────────────

    @GetMapping
    @Operation(
            summary = "[ADMIN/DOCTOR/NURSE] Get All Patients",
            description = """
            POSTMAN: GET http://localhost:8080/api/patients
            Header: Authorization: Bearer <ADMIN_or_DOCTOR_or_NURSE_TOKEN>
            No body needed.
            """
    )
    public ResponseEntity<ApiResponse<List<Patient>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("All patients", patientService.getAll()));
    }

    // ── Get one patient ───────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(
            summary = "[ANY] Get Patient Detail by ID",
            description = """
            POSTMAN: GET http://localhost:8080/api/patients/1
            Header: Authorization: Bearer <ANY_TOKEN>
            No body needed. Returns patient + all treatments.
            """
    )
    public ResponseEntity<ApiResponse<Patient>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Patient details", patientService.getById(id)));
    }

    // ── STEP 4 partial: Staff updates patient status ──────────────────────────

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "[DOCTOR/NURSE] Update Patient Status",
            description = """
            POSTMAN: PATCH http://localhost:8080/api/patients/1/status?status=STABLE
            Header: Authorization: Bearer <DOCTOR_or_NURSE_TOKEN>
            No body needed. Status is a query parameter.
            
            Valid values: ADMITTED | UNDER_OBSERVATION | STABLE | DISCHARGED
            
            On DISCHARGED:
            → dischargeDate set to today
            → Emergency status → CLOSED
            
            Example URLs:
            PATCH /api/patients/1/status?status=UNDER_OBSERVATION
            PATCH /api/patients/1/status?status=STABLE
            PATCH /api/patients/1/status?status=DISCHARGED
            """
    )
    public ResponseEntity<ApiResponse<Patient>> updateStatus(
            @PathVariable Long id,
            @RequestParam Patient.Status status) {

        Patient patient = patientService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Patient status updated to " + status, patient));
    }

    // ── Filter patients by status ─────────────────────────────────────────────

    @GetMapping("/status/{status}")
    @Operation(
            summary = "[ANY] Get Patients Filtered by Status",
            description = """
            POSTMAN examples:
            GET /api/patients/status/ADMITTED
            GET /api/patients/status/STABLE
            GET /api/patients/status/DISCHARGED
            Header: Authorization: Bearer <ANY_TOKEN>
            """
    )
    public ResponseEntity<ApiResponse<List<Patient>>> getByStatus(
            @PathVariable Patient.Status status) {

        return ResponseEntity.ok(
                ApiResponse.ok("Patients with status: " + status, patientService.getByStatus(status)));
    }
}
