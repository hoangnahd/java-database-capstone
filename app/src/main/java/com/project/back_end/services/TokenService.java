package com.project.back_end.services;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service utility responsible for generating, parsing, and validating 
 * JSON Web Tokens (JWT) used across the Clinical Management System security layer.
 */
@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Converts the configured plain-text JWT secret into a cryptographically secure HMAC Key.
     * * @return a {@link SecretKey} suitable for signing and verifying tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generates a securely signed JWT containing the user's email as its subject.
     * The token is timestamped and carries a 7-day expiration lifespan.
     *
     * @param email the user identification string to encode in the token payload
     * @return a compact, URL-safe JWT string
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) // 7 days expiration
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the subject email address from a provided JWT token.
     * This method features defensive error handling to intercept and handle malformed,
     * tampered, or expired signatures cleanly without breaking downstream application calls.
     *
     * @param token the JWT string to parse
     * @return the extracted email address if valid; {@code null} if the token is invalid, expired, or empty
     */
    public String extractEmail(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token.trim())
                    .getPayload()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            // Log the parsing failure details safely (Standard error fallback)
            System.err.println("JWT Parsing Exception: Token is malformed, expired, or invalid. " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates a JWT string token against database records based on a specific user context role.
     *
     * @param token the cryptographic token under evaluation
     * @param role  the target access level designation (e.g., "admin", "doctor", "patient")
     * @return {@code true} if the token is signature-valid, unexpired, and correlates to an active user record; {@code false} otherwise
     */
    public boolean validateToken(String token, String role) {
        if (token == null || token.isBlank() || role == null) {
            return false;
        }

        String email = extractEmail(token);
        
        // Fail fast if extractEmail returned null due to an internal exception (expired/malformed)
        if (email == null) {
            return false;
        }

        try {
            return switch (role.toLowerCase()) {
                case "admin" -> {
                    Admin admin = adminRepository.findByUsername(email);
                    yield admin != null;
                }
                case "doctor" -> {
                    Doctor doctor = doctorRepository.findByEmail(email);
                    yield doctor != null;
                }
                case "patient" -> {
                    Patient patient = patientRepository.findByEmail(email);
                    yield patient != null;
                }
                default -> false;
            };
        } catch (Exception e) {
            System.err.println("Database entity lookup error during token evaluation: " + e.getMessage());
            return false;
        }
    }
}
