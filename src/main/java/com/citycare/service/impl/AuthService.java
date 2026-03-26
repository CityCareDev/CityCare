package com.citycare.service.impl;

import com.citycare.dto.request.LoginRequest;
import com.citycare.dto.request.RegisterRequest;
import com.citycare.dto.response.AuthResponse;
import com.citycare.entity.Citizen;
import com.citycare.entity.User;
import com.citycare.exception.BadRequestException;
import com.citycare.repository.UserRepository;
import com.citycare.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================================
 * AuthService.java  –  Registration and Login Logic
 * ============================================================
 *
 * register():
 *   Only CITIZENS can self-register. Role is hardcoded to CITIZEN.
 *   Steps:
 *   1. Check email is not already registered (throws 400 if duplicate)
 *   2. Hash password with BCrypt
 *   3. Save User to DB → Hibernate does: INSERT INTO users (...)
 *   4. Generate JWT token
 *   5. Return token + user info → frontend stores the token
 *
 * login():
 *   Works for ALL roles (Citizen, Doctor, Nurse, Dispatcher, Admin).
 *   Steps:
 *   1. AuthenticationManager.authenticate() is called
 *      → internally calls UserDetailsService.loadUserByUsername(email)
 *      → compares BCrypt(input password) with stored hash
 *      → throws BadCredentialsException if wrong (caught by GlobalExceptionHandler)
 *   2. Generate new JWT token
 *   3. Return token + role → frontend uses role to navigate to correct dashboard
 *
 * ============================================================
 * HOW THIS FILE WORKS:
 *   Called by AuthController for /auth/register and /auth/login.
 *   @Transactional on register() → if DB save fails, everything rolls back.
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered: " + req.getEmail());
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword())) // BCrypt hash
                .role(User.Role.CITIZEN) // Hardcoded – cannot self-promote to ADMIN
                .phone(req.getPhone())
                .build();

        Citizen citizenProfile = Citizen.builder()
                .user(user) // Link Citizen to User
                .name(req.getName())
                .contactInfo(req.getPhone())
                .build();
        user.setCitizen(citizenProfile); // Set the bidirectional relationship

        userRepository.save(user); // Hibernate: INSERT INTO users (...)

        return AuthResponse.builder()
                .token(jwtUtils.generateToken(user.getEmail()))
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest req) {
        // Spring Security validates email+password → throws BadCredentialsException if invalid
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();

        return AuthResponse.builder()
                .token(jwtUtils.generateToken(user.getEmail()))
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole()) // Frontend checks this to decide which page to show
                .build();
    }
}