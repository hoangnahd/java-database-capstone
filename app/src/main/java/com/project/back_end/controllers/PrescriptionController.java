package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.TokenService;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final TokenService tokenService;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  TokenService tokenService,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.tokenService = tokenService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> savePrescription(@PathVariable String token,
                                                                 @RequestBody Prescription prescription) {
        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired doctor token."));
        }

        try {
            Long appointmentId = Long.parseLong(prescription.getAppointmentId());
            appointmentService.changeAppointmentStatus(appointmentId, 1);
        } catch (NumberFormatException ignored) {
            // ignore non-numeric appointment ids
        }

        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(@PathVariable String appointmentId,
                                                               @PathVariable String token) {
        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired doctor token."));
        }
        return prescriptionService.getPrescription(appointmentId);
    }
}
