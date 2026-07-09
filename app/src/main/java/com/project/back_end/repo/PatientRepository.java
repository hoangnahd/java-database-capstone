package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.back_end.models.Patient;

/**
 * Repository interface for managing {@link Patient} entities.
 * * By extending {@link JpaRepository}, this component inherits full CRUD 
 * functionality, pagination, and sorting capabilities from Spring Data JPA 
 * without requiring manual boilerplate implementation.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Retrieves a patient record uniquely identified by their email address.
     * Typically utilized for patient authentication, profile loading, or 
     * validating email uniqueness during registration.
     *
     * @param email the unique email address of the patient to search for
     * @return the {@link Patient} object if a match is found, or {@code null} if no match exists
     */
    Patient findByEmail(String email);

    /**
     * Retrieves a patient record matching either the provided email address OR phone number.
     * This custom query provides flexible lookup options, ensuring patients can be found 
     * using secondary contact channels or to prevent duplicate registrations using the same credentials.
     *
     * @param email the email address to search for
     * @param phone the phone number to search for
     * @return the {@link Patient} object if a match is found for either identifier, or {@code null} if none match
     */
    Patient findByEmailOrPhone(String email, String phone);
}
