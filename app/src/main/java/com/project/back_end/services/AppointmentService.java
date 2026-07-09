package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AppointmentService {

    // Removed @Transient and field-level @Autowired. 
    // Declared dependencies as final for safe constructor injection.
    private final AppointmentRepository appointmentRepository;
    private final BackendService service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // 2. Implemented Constructor Injection as requested by instructions
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            BackendService service,
            TokenService tokenService,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 4. Book Appointment Method
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch(Exception e) {
            return 0;
        }
    }

    // 5. Update Appointment Method with complete validation logic
    @Transactional
    public String updateAppointment(Appointment updatedAppointment) {
        Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(updatedAppointment.getId());
        if(existingAppointmentOpt.isEmpty()) {
            return "Error: appointment not found";
        }

        Appointment existingAppointment = existingAppointmentOpt.get();

        // Validation A: Check if patient ID matches the original record
        if (!existingAppointment.getPatient().getId().equals(updatedAppointment.getPatient().getId())) {
            return "Error: Patient ID mismatch. You cannot change the patient associated with this appointment.";
        }

        // Validation C: Ensure the doctor is available at the new specified time
        List<Appointment> conflictingAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
            updatedAppointment.getDoctor().getId(),
            updatedAppointment.getAppointmentTime().minusMinutes(1),
            updatedAppointment.getAppointmentTime().plusMinutes(1)
        );

        // Exclude the current appointment from flagging itself as a conflict
        boolean isDoctorBusy = conflictingAppointments.stream()
            .anyMatch(app -> !app.getId().equals(updatedAppointment.getId()));

        if (isDoctorBusy) {
            return "Error: The doctor is already booked at this specified time.";
        }

        // Save if all checks pass
        try {
            appointmentRepository.save(updatedAppointment);
            return "Success: Appointment updated successfully.";
        } catch (Exception e) {
            return "Error: A database failure occurred while updating the appointment.";
        }
    }

    // 6. Cancel Appointment Method
    @Transactional
    public String cancelAppointment(Appointment cancelAppointment) {
        Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(cancelAppointment.getId());
        
        if (existingAppointmentOpt.isEmpty()) {
            return "Error: Appointment not found.";
        }

        Appointment existingAppointment = existingAppointmentOpt.get();

        try {
            appointmentRepository.delete(existingAppointment);
            return "Success: Appointment canceled and removed successfully.";
        } catch (Exception e) {
            return "Error: A database failure occurred while canceling the appointment.";
        }
    }

    // 7. Get Appointments Method
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsForDoctorOnDay(Long doctorId, LocalDate date, String patientName) {
        LocalDateTime startOfDay = date.atStartOfDay(); 
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX); 

        if (patientName != null && !patientName.trim().isEmpty()) {
            return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                doctorId, patientName, startOfDay, endOfDay
            );
        } else {
            return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, startOfDay, endOfDay
            );
        }
    }

    // 8. Change Status Method
    @Transactional
    public String changeAppointmentStatus(long appointmentId, int newStatus) {
        try {
            boolean exists = appointmentRepository.existsById(appointmentId);
            if (!exists) {
                return "Error: Appointment not found.";
            }

            appointmentRepository.updateStatus(newStatus, appointmentId);
            return "Success: Appointment status updated successfully.";
        } catch (Exception e) {
            return "Error: Failed to update appointment status due to a database error.";
        }
    }
}