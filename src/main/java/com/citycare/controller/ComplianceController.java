package com.citycare.controller;

import com.citycare.dto.request.AuditRequest;
import com.citycare.dto.request.ComplianceRecordRequest;
import com.citycare.dto.response.ApiResponse;
import com.citycare.entity.Audit;
import com.citycare.entity.AuditLog;
import com.citycare.entity.ComplianceRecord;
import com.citycare.exception.ResourceNotFoundException;
import com.citycare.repository.UserRepository;
import com.citycare.service.ComplianceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compliance")
@RequiredArgsConstructor
@Tag(name = "8. Compliance & Audit", description = "Compliance records, audits, and audit logs")
public class ComplianceController {

    private final ComplianceService complianceService;
    private final UserRepository userRepository;

    // ── Compliance Records ────────────────────────────────────────────────────

    @PostMapping("/records")
    @Operation(summary = "[COMPLIANCE_OFFICER/ADMIN] Create Compliance Record")
    public ResponseEntity<ApiResponse<ComplianceRecord>> createRecord(
            @Valid @RequestBody ComplianceRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long officerId = resolveUserId(userDetails);
        ComplianceRecord record = complianceService.createRecord(officerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compliance record created", record));
    }

    @GetMapping("/records")
    @Operation(summary = "[ADMIN/COMPLIANCE_OFFICER] Get All Compliance Records")
    public ResponseEntity<ApiResponse<List<ComplianceRecord>>> getAllRecords() {
        return ResponseEntity.ok(ApiResponse.ok("All compliance records", complianceService.getAllRecords()));
    }

    @GetMapping("/records/{id}")
    @Operation(summary = "[ANY] Get Compliance Record by ID")
    public ResponseEntity<ApiResponse<ComplianceRecord>> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Compliance record", complianceService.getRecordById(id)));
    }

    @GetMapping("/records/entity/{entityId}")
    @Operation(summary = "[ANY] Get Compliance Records by Entity ID")
    public ResponseEntity<ApiResponse<List<ComplianceRecord>>> getByEntity(@PathVariable Long entityId) {
        return ResponseEntity.ok(ApiResponse.ok("Records for entity " + entityId, complianceService.getRecordsByEntity(entityId)));
    }

    @GetMapping("/records/type/{type}")
    @Operation(summary = "[ANY] Get Compliance Records by Type (FACILITY/PATIENT/EMERGENCY)")
    public ResponseEntity<ApiResponse<List<ComplianceRecord>>> getByType(@PathVariable ComplianceRecord.EntityType type) {
        return ResponseEntity.ok(ApiResponse.ok("Records by type: " + type, complianceService.getRecordsByType(type)));
    }

    // ── Audits ────────────────────────────────────────────────────────────────

    @PostMapping("/audits")
    @Operation(summary = "[COMPLIANCE_OFFICER/ADMIN] Create Audit")
    public ResponseEntity<ApiResponse<Audit>> createAudit(
            @Valid @RequestBody AuditRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long officerId = resolveUserId(userDetails);
        Audit audit = complianceService.createAudit(officerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Audit created", audit));
    }

    @GetMapping("/audits")
    @Operation(summary = "[ADMIN/COMPLIANCE_OFFICER] Get All Audits")
    public ResponseEntity<ApiResponse<List<Audit>>> getAllAudits() {
        return ResponseEntity.ok(ApiResponse.ok("All audits", complianceService.getAllAudits()));
    }

    @GetMapping("/audits/{id}")
    @Operation(summary = "[ANY] Get Audit by ID")
    public ResponseEntity<ApiResponse<Audit>> getAudit(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Audit details", complianceService.getAuditById(id)));
    }

    @PatchMapping("/audits/{id}/status")
    @Operation(summary = "[COMPLIANCE_OFFICER/ADMIN] Update Audit Status")
    public ResponseEntity<ApiResponse<Audit>> updateAuditStatus(
            @PathVariable Long id,
            @RequestParam Audit.Status status,
            @RequestParam(required = false) String findings) {
        Audit audit = complianceService.updateAuditStatus(id, status, findings);
        return ResponseEntity.ok(ApiResponse.ok("Audit updated", audit));
    }

    // ── Audit Logs ────────────────────────────────────────────────────────────

    @GetMapping("/logs")
    @Operation(summary = "[ADMIN] Get All Audit Logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getLogs() {
        return ResponseEntity.ok(ApiResponse.ok("Audit logs", complianceService.getAllLogs()));
    }

    @GetMapping("/logs/user/{userId}")
    @Operation(summary = "[ADMIN] Get Audit Logs by User")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Logs for user " + userId, complianceService.getLogsByUser(userId)));
    }

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"))
                .getUserId();
    }
}

