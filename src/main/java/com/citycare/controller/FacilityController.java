package com.citycare.controller;

import com.citycare.dto.request.FacilityRequest;
import com.citycare.dto.response.ApiResponse;
import com.citycare.entity.Facility;
import com.citycare.entity.Staff;
import com.citycare.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
@Tag(name = "7. Facilities", description = "Healthcare facility and staff management")
public class FacilityController {

    private final FacilityService facilityService;

    @PostMapping
    @Operation(summary = "[ADMIN] Create Facility")
    public ResponseEntity<ApiResponse<Facility>> create(
            @Valid @RequestBody FacilityRequest request) {
        Facility facility = facilityService.createFacility(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Facility created", facility));
    }

    @GetMapping
    @Operation(summary = "[ANY] Get All Facilities")
    public ResponseEntity<ApiResponse<List<Facility>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("All facilities", facilityService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "[ANY] Get Facility by ID")
    public ResponseEntity<ApiResponse<Facility>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Facility", facilityService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "[ADMIN] Update Facility")
    public ResponseEntity<ApiResponse<Facility>> update(
            @PathVariable Long id,
            @Valid @RequestBody FacilityRequest request) {
        Facility facility = facilityService.updateFacility(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Facility updated", facility));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "[ADMIN] Update Facility Status")
    public ResponseEntity<ApiResponse<Facility>> updateStatus(
            @PathVariable Long id,
            @RequestParam Facility.Status status) {
        Facility facility = facilityService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Facility status updated to " + status, facility));
    }

    @GetMapping("/{id}/staff")
    @Operation(summary = "[ADMIN] Get Staff at Facility")
    public ResponseEntity<ApiResponse<List<Staff>>> getStaff(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Staff at facility", facilityService.getStaffByFacility(id)));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "[ANY] Get Facilities by Type (HOSPITAL or CLINIC)")
    public ResponseEntity<ApiResponse<List<Facility>>> getByType(@PathVariable Facility.Type type) {
        return ResponseEntity.ok(ApiResponse.ok("Facilities by type: " + type, facilityService.getByType(type)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "[ANY] Get Facilities by Status")
    public ResponseEntity<ApiResponse<List<Facility>>> getByStatus(@PathVariable Facility.Status status) {
        return ResponseEntity.ok(ApiResponse.ok("Facilities with status: " + status, facilityService.getByStatus(status)));
    }
}
