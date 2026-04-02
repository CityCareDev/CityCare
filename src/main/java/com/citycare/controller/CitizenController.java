package com.citycare.controller;

import com.citycare.dto.request.CitizenProfileRequest;
import com.citycare.dto.response.ApiResponse;
import com.citycare.entity.Citizen;
import com.citycare.entity.CitizenDocument;
import com.citycare.exception.ResourceNotFoundException;
import com.citycare.repository.UserRepository;
import com.citycare.service.CitizenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/citizens")
@RequiredArgsConstructor
public class CitizenController {

    private final CitizenService citizenService;
    private final UserRepository userRepository;

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Citizen>> upsertProfile(
            @Valid @RequestBody CitizenProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        Citizen citizen = citizenService.createOrUpdateProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Profile saved", citizen));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Citizen>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Citizen profile", citizenService.getProfile(userId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Citizen>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("All citizens", citizenService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Citizen>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Citizen details", citizenService.getById(id)));
    }

    // --- Citizen Document Part with MultipartFile ---

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CitizenDocument>> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            // Extract bytes from MultipartFile to store in @Lob field
            byte[] documentBytes = file.getBytes();
            CitizenDocument doc = citizenService.uploadDocument(id, documentBytes);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Document uploaded", doc));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to process file: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<ApiResponse<List<CitizenDocument>>> getDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Documents", citizenService.getDocuments(id)));
    }

    @PatchMapping("/documents/{docId}/verify")
    public ResponseEntity<ApiResponse<CitizenDocument>> verifyDocument(
            @PathVariable Long docId,
            @RequestParam CitizenDocument.VerificationStatus status) {
        CitizenDocument doc = citizenService.verifyDocument(docId, status);
        return ResponseEntity.ok(ApiResponse.ok("Document status updated to " + status, doc));
    }

    // Helper to resolve UserID from JWT details
    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"))
                .getUserId();
    }
}
