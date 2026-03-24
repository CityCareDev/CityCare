package com.citycare.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ============================================================
 * JwtAuthFilter.java  –  JWT Validation on Every Request
 * ============================================================
 *
 * This filter runs once per HTTP request (OncePerRequestFilter).
 * It intercepts every request BEFORE Spring Security's default filter.
 *
 * WHAT IT DOES PER REQUEST:
 *   1. Read "Authorization" header: "Bearer eyJhbGci..."
 *   2. Extract the token (remove "Bearer " prefix)
 *   3. Validate token signature + expiry via JwtUtils
 *   4. Extract email from token payload
 *   5. Load User from DB by email
 *   6. Create an authentication object and set it in SecurityContextHolder
 *   7. Spring Security now knows: "this request is from user X with role Y"
 *   8. Proceed to the controller
 *
 * IF TOKEN IS MISSING OR INVALID:
 *   SecurityContextHolder stays empty → Spring Security returns 401
 *
 * ============================================================
 * HOW THIS FILE WORKS:
 *   Registered in SecurityConfig via:
 *     .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
 *   Runs on every request. Public paths (/auth/**) still pass through
 *   this filter but their endpoints are marked permitAll() so even
 *   unauthenticated requests can reach them.
 * ============================================================
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String jwt = extractJwt(request);

            if (jwt != null && jwtUtils.validateToken(jwt)) {
                String email = jwtUtils.getEmailFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Create authentication token with the user's authorities (roles)
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                          // no credentials needed (JWT already validated)
                                userDetails.getAuthorities()   // ["ROLE_CITIZEN"] etc.
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Tell Spring Security: this request belongs to an authenticated user
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage());
        }

        chain.doFilter(request, response); // Hand off to the next filter / controller
    }

    private String extractJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        // Token header format: "Bearer eyJhbGci..."
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7); // Remove "Bearer " (7 chars)
        }
        return null;
    }
}
