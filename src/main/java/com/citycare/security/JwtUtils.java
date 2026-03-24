package com.citycare.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * ============================================================
 * JwtUtils.java  –  JWT Token Creation and Validation
 * ============================================================
 *
 * WHAT IS A JWT?
 *   A JSON Web Token is a compact, signed string like:
 *     eyJhbGci... . eyJ1c2VyIjoiYWRtaW4ifQ . SflKxw...
 *     [HEADER]     [PAYLOAD (readable!)]     [SIGNATURE]
 *
 *   The PAYLOAD contains the user's email and expiry time.
 *   Anyone can READ it (it's Base64 encoded, not encrypted).
 *   But only our server can VERIFY it because only we have the secret key.
 *   If someone modifies the payload, the signature breaks → rejected.
 *
 * HOW IT'S USED IN THIS APP:
 *   1. User logs in → generateToken(email) returns a signed token string
 *   2. Frontend stores the token and sends it with every request:
 *        Authorization: Bearer eyJhbGci...
 *   3. JwtAuthFilter calls validateToken() + getEmailFromToken()
 *   4. Spring Security loads user from DB by email and sets auth context
 *
 * ============================================================
 * HOW THIS FILE WORKS:
 *   generateToken()       → builds + signs a new JWT string
 *   getEmailFromToken()   → decodes JWT and extracts the email
 *   validateToken()       → checks signature + expiry
 * ============================================================
 */
@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // Convert the secret string into a cryptographic HMAC-SHA256 signing key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)                          // who this token is for
                .setIssuedAt(new Date())                    // when it was created
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // 24h expiry
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)                   // sign it
                .compact();                                  // serialize to string
    }

    public String getEmailFromToken(String token) {
        // parseClaimsJws verifies the signature first, then returns the payload
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // the email we stored in setSubject()
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }
}
