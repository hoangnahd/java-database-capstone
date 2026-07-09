package com.project.back_end.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;

@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    private final PatientService patientService;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;

    public PatientController(PatientService patientService, TokenService tokenService, PatientRepository patientRepository) {
        this.patientService = patientService;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPatient(@RequestBody Patient patient) {
        int result = patientService.createPatient(patient);
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Patient created successfully."));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unable to create patient."));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.getOrDefault("email", "").trim();
        String password = credentials.getOrDefault("password", "").trim();

        if (email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required."));
        }

        Patient patient = patientRepository.findByEmail(email);
        if (patient == null || !password.equals(patient.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials."));
        }

        String token = tokenService.generateToken(email);
        return ResponseEntity.ok(Map.of("message", "Login successful", "token", token));
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired patient token."));
        }
        return patientService.getPatientDetails(token);
    }

    @GetMapping("/{id}/{user}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(@PathVariable Long id,
                                                                     @PathVariable String user,
                                                                     @PathVariable String token) {
        if (!tokenService.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token."));
        }
        return patientService.getPatientAppointment(id);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                        @PathVariable String name,
                                                                        @PathVariable String token) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token."));
        }

        String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);
        if (decodedName == null || "null".equalsIgnoreCase(decodedName)) {
            decodedName = "";
        }

        return patientService.getPatientAppointment(1L);
    }
}


