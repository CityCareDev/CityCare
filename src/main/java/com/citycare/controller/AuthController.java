package com.citycare.controller;

import com.citycare.dto.request.LoginRequest;
import com.citycare.dto.request.RegisterRequest;
import com.citycare.dto.response.ApiResponse;
import com.citycare.dto.response.AuthResponse;
import com.citycare.entity.User;
import com.citycare.repository.UserRepository;
import com.citycare.service.AuthService;
import com.citycare.service.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "Register and Login via JSON.")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registration successful", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login via JSON body", description = "Verifies credentials and returns user role.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        // 1. Manually authenticate using JSON body
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        // 1. Manually authenticate using JSON body
//        Authentication authentication = authenticationManager
        //              .authenticate(new UsernamePasswordAuthenticationToken(user.getUserID().toString(),user.getPassword()));


        // 2. Fetch user data if authentication was successful
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // generate jwt token
        String token = null;
        if(authentication.isAuthenticated()){
            token = jwtService.generateToken(user.getEmail());
            System.out.println(token);
        }


        AuthResponse response = AuthResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                // .token is gone
                .token(token)
                .build();

        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }
}