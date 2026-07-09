package com.project.back_end.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.TokenService;

@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TokenService tokenService;
    private final DoctorRepository doctorRepository;

    public AppointmentController(AppointmentService appointmentService,
                                 TokenService tokenService,
                                 DoctorRepository doctorRepository) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                               @PathVariable String patientName,
                                                               @PathVariable String token) {
        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired doctor token."));
        }

        String email = tokenService.extractEmail(token);
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Doctor not found."));
        }

        String decodedName = URLDecoder.decode(patientName, StandardCharsets.UTF_8);
        if ("null".equalsIgnoreCase(decodedName)) {
            decodedName = "";
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        List<Appointment> appointments = appointmentService.getAppointmentsForDoctorOnDay(doctor.getId(), appointmentDate, decodedName);
        return ResponseEntity.ok(Map.of("appointments", appointments));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(@PathVariable String token,
                                                               @RequestBody Appointment appointment) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired patient token."));
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Appointment booked successfully."));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unable to book appointment."));
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(@PathVariable String token,
                                                                 @RequestBody Appointment appointment) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired patient token."));
        }

        String message = appointmentService.updateAppointment(appointment);
        if (message.startsWith("Success")) {
            return ResponseEntity.ok(Map.of("message", message));
        }
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
