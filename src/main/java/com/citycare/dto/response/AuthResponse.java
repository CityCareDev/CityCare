package com.citycare.dto.response;

import com.citycare.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================================
 * AuthResponse.java  –  Returned after login or registration
 * ============================================================
 *
 * The frontend receives this after successful auth.
 *
 * WHAT FRONTEND SHOULD DO WITH THIS:
 *   1. Store the 'token' in localStorage or a cookie
 *   2. Check 'role' to decide which page/dashboard to show:
 *        CITIZEN    → show citizen dashboard (emergency history)
 *        DISPATCHER → show dispatcher console (pending emergencies)
 *        ADMIN      → show admin panel (dispatched list, patient admission)
 *        DOCTOR     → show doctor dashboard (patient list, treatments)
 *        NURSE      → show nurse dashboard (patient list, treatments)
 *   3. Add to every future request header:
 *        Authorization: Bearer <token>
 *
 * ============================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private User.Role role;
}