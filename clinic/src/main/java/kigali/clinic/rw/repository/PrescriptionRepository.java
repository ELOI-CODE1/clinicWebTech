package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByAppointment(Appointment appointment);

    boolean existsByAppointment_Id(UUID appointmentId);

    @Query("""
        SELECT pres FROM Prescription pres
        WHERE pres.appointment.patient.ssNumber = :ssNumber
        ORDER BY pres.issuedDate DESC
        """)
    List<Prescription> findByPatientSsNumber(@Param("ssNumber") String ssNumber);
}
