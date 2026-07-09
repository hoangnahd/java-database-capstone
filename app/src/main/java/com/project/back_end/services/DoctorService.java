package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 4. getDoctorAvailability
    @Transactional
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Doctor not found"));
        }

        Doctor doctor = doctorOpt.get();
        List<String> allSlots = doctor.getAvailableTimes();

        // Build day boundary range to match the repo method signature
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(LocalTime.MAX);

        List<Appointment> booked =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        List<String> bookedSlots = booked.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        List<String> availableSlots = allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "doctorId", doctorId,
                "date", date.toString(),
                "availableSlots", availableSlots
        ));
    }

    // 5. saveDoctor
    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existing = doctorRepository.findByEmail(doctor.getEmail());
            if (existing != null) {
                return -1; // conflict: email already exists
            }
            doctorRepository.save(doctor);
            return 1; // success
        } catch (Exception e) {
            return 0; // internal error
        }
    }

    // 6. updateDoctor
    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId())) {
                return -1; // doctor not found
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 7. getDoctors
    @Transactional
    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        // Force eager load of availableTimes in case of lazy relationship
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 8. deleteDoctor
    public int deleteDoctor(Long doctorId) {
        try {
            if (!doctorRepository.existsById(doctorId)) {
                return -1; // not found
            }
            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 9. validateDoctor
    public ResponseEntity<Map<String, Object>> validateDoctor(String email, String password) {
        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Doctor not found"));
        }

        if (!doctor.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        String token = tokenService.generateToken(email);
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "token", token
        ));
    }

    // 10. findDoctorByName
    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        // Eagerly load available times
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 11. filterDoctorsByNameSpecilityandTime
    @Transactional
    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String time) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return filterDoctorByTime(doctors, time);
    }

    // 12. filterDoctorByTime (helper — filters a given list by AM/PM)
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String time) {
        return doctors.stream()
                .filter(d -> d.getAvailableTimes().stream()
                        .anyMatch(slot -> matchesTimePeriod(slot, time)))
                .collect(Collectors.toList());
    }

    // 13. filterDoctorByNameAndTime
    @Transactional
    public List<Doctor> filterDoctorByNameAndTime(String name, String time) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return filterDoctorByTime(doctors, time);
    }

    // 14. filterDoctorByNameAndSpecility
    @Transactional
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 15. filterDoctorByTimeAndSpecility
    @Transactional
    public List<Doctor> filterDoctorByTimeAndSpecility(String specialty, String time) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return filterDoctorByTime(doctors, time);
    }

    // 16. filterDoctorBySpecility
    @Transactional
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 17. filterDoctorsByTime
    @Transactional
    public List<Doctor> filterDoctorsByTime(String time) {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(d -> d.getAvailableTimes().size());
        return filterDoctorByTime(doctors, time);
    }

    // --- Private helper ---

    /**
     * Returns true if the given time slot string falls within the requested period.
     * Expects slot in "HH:mm" format; period is "AM" or "PM".
     */
    private boolean matchesTimePeriod(String slot, String period) {
        try {
            LocalTime t = LocalTime.parse(slot);
            if ("AM".equalsIgnoreCase(period)) {
                return t.getHour() < 12;
            } else if ("PM".equalsIgnoreCase(period)) {
                return t.getHour() >= 12;
            }
        } catch (Exception ignored) {
            // malformed slot — skip
        }
        return false;
    }
}