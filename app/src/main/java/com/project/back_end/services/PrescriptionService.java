package com.project.back_end.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

// 1. Add @Service Annotation
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    // 2. Constructor Injection for Dependencies
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // 3. & 5. savePrescription Method with Exception Handling
    public ResponseEntity<Map<String, Object>> savePrescription(Prescription prescription) {
        Map<String, Object> response = new HashMap<>();
        try {
            String appointmentId = prescription.getAppointmentId();

            // Check if prescription already exists for this appointment
            boolean exists = prescriptionRepository.existsByAppointmentId(appointmentId);
            
            if (exists) {
                response.put("error", "A prescription already exists for this appointment.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 Bad Request
            }

            // Save new prescription
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription created successfully.");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created

        } catch (Exception e) {
            // Log the error in a real app using a Logger (e.g., log.error(e.getMessage()); )
            response.put("error", "An internal error occurred while saving the prescription.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Error
        }
    }

    // 4. & 5. getPrescription Method with Exception Handling
    public ResponseEntity<Map<String, Object>> getPrescription(String appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Fetch the prescription
            Optional<Prescription> prescriptionOpt = prescriptionRepository.findByAppointmentId(appointmentId);

            if (prescriptionOpt.isPresent()) {
                response.put("prescription", prescriptionOpt.get());
                return ResponseEntity.ok(response); // 200 OK
            } else {
                // Handling the edge case where no prescription is found
                response.put("message", "No prescription found for the specified appointment.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found
            }

        } catch (Exception e) {
            // Log the error in a real app using a Logger
            response.put("error", "An internal error occurred while retrieving the prescription.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Error
        }
    }
}