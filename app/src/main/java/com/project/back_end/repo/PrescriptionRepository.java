package com.project.back_end.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Prescription;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
// 1. Extend MongoRepository:
//    - The repository extends MongoRepository<Prescription, String>, which provides basic CRUD functionality for MongoDB.
//    - This allows the repository to perform operations like save, delete, update, and find without needing to implement these methods manually.
//    - MongoRepository is tailored for working with MongoDB, unlike JpaRepository which is used for relational databases.

// Example: public interface PrescriptionRepository extends MongoRepository<Prescription, String> {}

// 2. Custom Query Method:

//    - **findByAppointmentId**:
//      - This method retrieves a prescription associated with a specific appointment.
//      - Return type: Optional<Prescription>
//      - Parameters: String appointmentId
//      - MongoRepository automatically derives the query from the method name, in this case, it will find a prescription by the appointment ID.
    Optional<Prescription> findByAppointmentId(String appointmentId);
    boolean existsByAppointmentId(String appointmentId);

}

