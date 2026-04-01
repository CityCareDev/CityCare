package com.citycare.service;

import com.citycare.dto.request.LoginRequest;
import com.citycare.dto.request.RegisterRequest;
import com.citycare.dto.response.AuthResponse;
import com.citycare.entity.Citizen;
import com.citycare.entity.User;
import com.citycare.exception.BadRequestException;
import com.citycare.repository.CitizenRepository;
import com.citycare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CitizenRepository citizenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered: " + req.getEmail());
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword())) // BCrypt hash
                .role(User.Role.CITIZEN)// Hardcoded – cannot self-promote to ADMIN
                .status(User.Status.ACTIVE) // Default status
                .phone(req.getPhone())
                .build();

        User savedUser = userRepository.save(user);

        // 3. MANDATORY: Create and Save the Citizen record linked to this User
        // This ensures the data appears in the 'citizens' table immediately
        Citizen citizen = Citizen.builder()
                .name(savedUser.getName())
                .contactInfo(savedUser.getPhone())
                .user(savedUser) // Linking the Foreign Key
                .status(Citizen.Status.ACTIVE)
                .build();

        citizenRepository.save(citizen);


        return mapToAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToAuthResponse(user);
    }

    private AuthResponse mapToAuthResponse(User user) {
        return AuthResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}