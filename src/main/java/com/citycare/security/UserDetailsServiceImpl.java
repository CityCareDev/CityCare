package com.citycare.security;

import com.citycare.entity.User;
import com.citycare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * UserDetailsServiceImpl.java  –  Load User for Spring Security
 * ============================================================
 *
 * Spring Security calls loadUserByUsername() during EVERY
 * authenticated request (after the JWT is validated).
 *
 * Despite "ByUsername" in the method name, we use EMAIL as
 * the unique identifier (Spring allows any string).
 *
 * HOW THIS FILE WORKS:
 *   1. JwtAuthFilter extracts email from the JWT token
 *   2. Calls this: loadUserByUsername(email)
 *   3. We do: SELECT * FROM users WHERE email = ?
 *   4. Convert to Spring's UserDetails with granted authority:
 *        Role.CITIZEN    → "ROLE_CITIZEN"
 *        Role.DISPATCHER → "ROLE_DISPATCHER"
 *        Role.ADMIN      → "ROLE_ADMIN"
 *        etc.
 *   5. Spring Security checks if this authority matches the
 *      .hasRole("ADMIN") rules defined in SecurityConfig
 *
 * NOTE: hasRole("ADMIN") in SecurityConfig auto-prepends "ROLE_"
 * so "ROLE_ADMIN" matches hasRole("ADMIN") – Spring convention.
 *
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Build Spring Security's UserDetails from our User entity
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // BCrypt hash – Spring compares during login
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}