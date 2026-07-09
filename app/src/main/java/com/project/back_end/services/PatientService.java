package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository     = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService          = tokenService;
    }

    // 3. createPatient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 4. getPatientAppointment
    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("appointments", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to retrieve appointments"));
        }
    }

    // 5. filterByCondition — "past" maps to status 1, "future" maps to status 0
    public ResponseEntity<Map<String, Object>> filterByCondition(Long patientId, String condition) {
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid condition. Use 'past' or 'future'"));
            }

            List<Appointment> appointments =
                    appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("appointments", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to filter appointments"));
        }
    }

    // 6. filterByDoctor
    public ResponseEntity<Map<String, Object>> filterByDoctor(String doctorName, Long patientId) {
        try {
            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("appointments", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to filter appointments by doctor"));
        }
    }

    // 7. filterByDoctorAndCondition
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String doctorName,
                                                                           Long patientId,
                                                                           String condition) {
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid condition. Use 'past' or 'future'"));
            }

            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("appointments", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to filter appointments by doctor and condition"));
        }
    }

    // 8. getPatientDetails
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Patient not found"));
            }
            return ResponseEntity.ok(Map.of("patient", patient));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to retrieve patient details"));
        }
    }

    // --- Private helper ---

    /**
     * Maps an Appointment entity to an AppointmentDTO.
     * Pulls flattened doctor/patient fields to avoid exposing full entity graphs.
     */
    private AppointmentDTO toDTO(Appointment a) {
        return new AppointmentDTO(
                a.getId(),
                a.getDoctor().getId(),
                a.getDoctor().getName(),
                a.getPatient().getId(),
                a.getPatient().getName(),
                a.getPatient().getEmail(),
                a.getPatient().getPhone(),
                a.getPatient().getAddress(),
                a.getAppointmentTime(),
                a.getStatus()
        );
    }
}