package com.citycare.controller;

import com.citycare.dto.request.DispatchRequest;
import com.citycare.dto.request.EmergencyRequest;
import com.citycare.dto.response.ApiResponse;
import com.citycare.entity.Ambulance;
import com.citycare.entity.Emergency;
import com.citycare.entity.User;
import com.citycare.exception.ResourceNotFoundException;
import com.citycare.repository.UserRepository;
import com.citycare.service.EmergencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emergencies")
@RequiredArgsConstructor
@Tag(name = "2. Emergency", description = "Step 1: Citizen reports. Step 2: Dispatcher views + assigns ambulance.")
public class EmergencyController {

    private final EmergencyService emergencyService;
    private final UserRepository userRepository;

    // ── STEP 1: Citizen reports an emergency ─────────────────────────────────

    @PostMapping("/report")
    @Operation(
            summary = "[CITIZEN] Report an Emergency – Step 1",
            description = """
            POSTMAN: POST http://localhost:8080/api/emergencies/report
            Header: Authorization: Bearer <CITIZEN_TOKEN>
            Body:
            {
              "type": "ACCIDENT",
              "location": "Anna Nagar, Chennai - Near Metro Station",
              "description": "Road accident, 2 people injured"
            }
            Types: ACCIDENT | HEART_ATTACK | FIRE | STROKE | FALL | OTHER
            → After this, Dispatcher sees it in GET /emergencies/pending
            """
    )
    public ResponseEntity<ApiResponse<Emergency>> report(
            @Valid @RequestBody EmergencyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long citizenId = resolveUserId(userDetails);
        Emergency emergency = emergencyService.reportEmergency(citizenId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Emergency reported. Help is on the way.", emergency));
    }

    // ── STEP 2a: Dispatcher views all pending (REPORTED) emergencies ──────────

    @GetMapping("/pending")
    @Operation(
            summary = "[DISPATCHER] View All Pending Emergencies – Step 2a",
            description = """
            POSTMAN: GET http://localhost:8080/api/emergencies/pending
            Header: Authorization: Bearer <DISPATCHER_TOKEN>
            No body needed.
            → Returns all REPORTED emergencies (newest first).
            → Frontend polls this endpoint every 10–30s to show dispatcher alerts.
            → Note the 'emergencyId' from results – needed for dispatch step.
            """
    )
    public ResponseEntity<ApiResponse<List<Emergency>>> getPending() {
        return ResponseEntity.ok(
                ApiResponse.ok("Pending emergencies", emergencyService.getReportedEmergencies()));
    }

    // ── STEP 2b: Dispatcher checks which ambulances are free ──────────────────

    @GetMapping("/ambulances/available")
    @Operation(
            summary = "[DISPATCHER] Get Available Ambulances – Step 2b",
            description = """
            POSTMAN: GET http://localhost:8080/api/emergencies/ambulances/available
            Header: Authorization: Bearer <DISPATCHER_TOKEN>
            No body needed.
            → Returns all ambulances with status = AVAILABLE.
            → Pick an 'ambulanceId' from this list to use in the dispatch step.
            → If empty list: all ambulances are DISPATCHED or under MAINTENANCE.
            """
    )
    public ResponseEntity<ApiResponse<List<Ambulance>>> getAvailableAmbulances() {
        return ResponseEntity.ok(
                ApiResponse.ok("Available ambulances", emergencyService.getAvailableAmbulances()));
    }

    // ── STEP 2c: Dispatcher assigns ambulance to emergency ────────────────────

    @PostMapping("/{emergencyId}/dispatch")
    @Operation(
            summary = "[DISPATCHER] Assign Ambulance to Emergency – Step 2c",
            description = """
            POSTMAN: POST http://localhost:8080/api/emergencies/1/dispatch
            (Replace '1' with the actual emergencyId from /emergencies/pending)
            Header: Authorization: Bearer <DISPATCHER_TOKEN>
            Body:
            {
              "ambulanceId": 1
            }
            (Use ambulanceId from GET /emergencies/ambulances/available)
            
            WHAT HAPPENS:
            → ambulance.status = DISPATCHED (removed from available list)
            → emergency.status = DISPATCHED
            → Admin sees it in GET /emergencies/dispatched
            
            ERROR if ambulance not AVAILABLE:
            { "message": "Ambulance TN-01-AB-1234 is not available (current status: DISPATCHED)" }
            """
    )
    public ResponseEntity<ApiResponse<Emergency>> dispatch(
            @PathVariable Long emergencyId,
            @Valid @RequestBody DispatchRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long dispatcherId = resolveUserId(userDetails);
        Emergency emergency = emergencyService.dispatchAmbulance(emergencyId, dispatcherId, request);
        return ResponseEntity.ok(ApiResponse.ok("Ambulance dispatched successfully", emergency));
    }

    // ── ADMIN VIEW: See dispatched emergencies ready for patient admission ─────

    @GetMapping("/dispatched")
    @Operation(
            summary = "[ADMIN] View Dispatched Emergencies (Ready for Patient Admission)",
            description = """
            POSTMAN: GET http://localhost:8080/api/emergencies/dispatched
            Header: Authorization: Bearer <ADMIN_TOKEN>
            No body needed.
            → Returns all DISPATCHED emergencies.
            → Admin uses emergencyId from this list to call POST /patients/admit.
            → Frontend polls this every 10–30s to show admin admission alerts.
            """
    )
    public ResponseEntity<ApiResponse<List<Emergency>>> getDispatched() {
        return ResponseEntity.ok(
                ApiResponse.ok("Dispatched emergencies – ready for patient admission",
                        emergencyService.getDispatchedEmergencies()));
    }

    // ── Citizen views their own history ──────────────────────────────────────

    @GetMapping("/my")
    @Operation(
            summary = "[CITIZEN] View My Emergency History",
            description = """
            POSTMAN: GET http://localhost:8080/api/emergencies/my
            Header: Authorization: Bearer <CITIZEN_TOKEN>
            No body needed.
            → Returns all emergencies reported by the logged-in citizen.
            """
    )
    public ResponseEntity<ApiResponse<List<Emergency>>> getMyCases(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long citizenId = resolveUserId(userDetails);
        return ResponseEntity.ok(
                ApiResponse.ok("Your emergency history", emergencyService.getMyCases(citizenId)));
    }

    // ── Get one emergency by ID ───────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(
            summary = "[ANY] Get Emergency Detail by ID",
            description = """
            POSTMAN: GET http://localhost:8080/api/emergencies/1
            Header: Authorization: Bearer <ANY_VALID_TOKEN>
            No body needed.
            → Returns full detail of one emergency including citizen, dispatcher, ambulance info.
            """
    )
    public ResponseEntity<ApiResponse<Emergency>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Emergency details", emergencyService.getById(id)));
    }

    // ── Helper: resolve JWT email → userId ───────────────────────────────────

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"))
                .getUserId();
    }
}
