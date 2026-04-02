package com.citycare.controller;
import com.citycare.dto.request.AmbulanceRequest;
import com.citycare.dto.request.CreateStaffRequest;
import com.citycare.dto.response.ApiResponse;
import com.citycare.entity.Ambulance;
import com.citycare.entity.User;
import com.citycare.repository.AmbulanceRepository;
import com.citycare.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "5. Admin", description = "Admin only: Create staff/dispatchers, manage ambulances and users. ALL require ADMIN token.")
public class AdminController {

    private final AdminService adminService;
    private final AmbulanceRepository ambulanceRepository;

    // ── STAFF ─────────────────────────────────────────────────────────────────

    @PostMapping("/staff")
    @Operation(summary = "[ADMIN] Create Doctor or Nurse Account")
    public ResponseEntity<ApiResponse<User>> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Staff account created", adminService.createStaff(request)));
    }

    @GetMapping("/staff")
    @Operation(summary = "[ADMIN] List All Doctors and Nurses")
    public ResponseEntity<ApiResponse<List<User>>> getAllStaff() {
        return ResponseEntity.ok(ApiResponse.ok("All staff", adminService.getAllStaff()));
    }

    // ── DISPATCHERS ───────────────────────────────────────────────────────────

    @PostMapping("/dispatchers")
    @Operation(summary = "[ADMIN] Create Dispatcher Account")
    public ResponseEntity<ApiResponse<User>> createDispatcher(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Dispatcher account created", adminService.createDispatcher(request)));
    }

    @GetMapping("/dispatchers")
    @Operation(summary = "[ADMIN] List All Dispatchers")
    public ResponseEntity<ApiResponse<List<User>>> getAllDispatchers() {
        return ResponseEntity.ok(ApiResponse.ok("All dispatchers", adminService.getAllDispatchers()));
    }

    // ── COMPLIANCE / HEALTH OFFICERS ──────────────────────────────────────────

    @PostMapping("/compliance-officers")
    @Operation(summary = "[ADMIN] Create Compliance Officer Account")
    public ResponseEntity<ApiResponse<User>> createComplianceOfficer(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compliance officer created", adminService.createComplianceOfficer(request)));
    }

    @PostMapping("/health-officers")
    @Operation(summary = "[ADMIN] Create City Health Officer Account")
    public ResponseEntity<ApiResponse<User>> createHealthOfficer(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("City health officer created", adminService.createCityHealthOfficer(request)));
    }

    // ── AMBULANCES ────────────────────────────────────────────────────────────

    @PostMapping("/ambulances")
    @Operation(summary = "[ADMIN] Add Ambulance to Fleet")
    public ResponseEntity<ApiResponse<Ambulance>> addAmbulance(@Valid @RequestBody AmbulanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Ambulance added to fleet", adminService.addAmbulance(request)));
    }

    @GetMapping("/ambulances")
    @Operation(summary = "[ADMIN] List All Ambulances")
    public ResponseEntity<ApiResponse<List<Ambulance>>> getAllAmbulances() {
        return ResponseEntity.ok(ApiResponse.ok("All ambulances", adminService.getAllAmbulances()));
    }

    @PatchMapping("/ambulances/{id}/status")
    @Operation(summary = "[ADMIN] Update Ambulance Status (MAINTENANCE / AVAILABLE)")
    public ResponseEntity<ApiResponse<Ambulance>> updateAmbulanceStatus(
            @PathVariable Long id, @RequestParam Ambulance.Status status) {
        return ResponseEntity.ok(ApiResponse.ok("Ambulance status updated to " + status,
                adminService.updateAmbulanceStatus(id, status)));
    }

    // ── USER MANAGEMENT ───────────────────────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "[ADMIN] List All Users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("All users", adminService.getAllUsers()));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "[ADMIN] Get User by ID")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User", adminService.getUserById(id)));
    }

    @PatchMapping("/users/{id}/deactivate")
    @Operation(summary = "[ADMIN] Deactivate a User Account")
    public ResponseEntity<ApiResponse<User>> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User deactivated", adminService.deactivateUser(id)));
    }

    @PatchMapping("/users/{id}/activate")
    @Operation(summary = "[ADMIN] Activate a User Account")
    public ResponseEntity<ApiResponse<User>> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User activated", adminService.activateUser(id)));
    }
}