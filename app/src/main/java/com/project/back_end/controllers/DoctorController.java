package com.project.back_end.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.getOrDefault("email", "").trim();
        String password = credentials.getOrDefault("password", "").trim();

        if (email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required."));
        }

        return doctorService.validateDoctor(email, password);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(@PathVariable String token,
                                                           @RequestBody Doctor doctor) {
        // Token validation logic
        if (token == null || token.isBlank() || !doctorService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or missing token. Authorization denied."));
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Doctor created successfully."));
        }

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Doctor already exists."));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Unable to save doctor."));
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterDoctors(@RequestParam(defaultValue = "") String name,
                                                             @RequestParam(defaultValue = "") String time,
                                                             @RequestParam(defaultValue = "") String specialty) {
        List<Doctor> doctors = doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    /**
     * Retrieves a doctor's availability based on user role, doctor ID, date, and security token.
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(@PathVariable String user,
                                                                     @PathVariable Long doctorId,
                                                                     @PathVariable String date,
                                                                     @PathVariable String token) {
        // Critical Requirement: Token Validation
        if (token == null || token.isBlank() || !doctorService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token. Access denied."));
        }

        // Fetch availability from service layer using the provided parameters
        List<Map<String, Object>> availability = doctorService.getDoctorAvailability(user, doctorId, date);

        if (availability == null || availability.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No availability found for the given criteria."));
        }

        return ResponseEntity.ok(Map.of("availability", availability));
    }
}
