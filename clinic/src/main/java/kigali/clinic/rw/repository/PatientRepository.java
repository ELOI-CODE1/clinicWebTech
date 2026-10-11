package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByFirstName(String firstName);

    List<Patient> findByLastName(String lastName);

    Optional<Patient> findByFirstNameAndLastName(String firstName, String lastName);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    Optional<Patient> findBySsNumber(String ssNumber);

    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    @Query("""
        SELECT DISTINCT ap.patient FROM Appointment ap
        WHERE ap.doctor.id = :doctorId
        """)
    List<Patient> findPatientsOfDoctor(@Param("doctorId") UUID doctorId);

    @Query("""
        SELECT ap.patient FROM Appointment ap
        GROUP BY ap.patient
        HAVING COUNT(ap) >= :min
        ORDER BY COUNT(ap) DESC
        """)
    List<Patient> findFrequentPatients(@Param("min") long min);
}
