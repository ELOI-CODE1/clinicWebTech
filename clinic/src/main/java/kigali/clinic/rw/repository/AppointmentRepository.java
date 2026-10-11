package kigali.clinic.rw.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByAppointmentDate(Date appointmentDate);

    Optional<Appointment> findByReasonAndAppointmentDate(String reason, Date appointmentDate);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(java.sql.Date start, java.sql.Date end);

    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(UUID doctorId, java.sql.Date appointmentDate, AppointmentStatus status);

    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNot(UUID doctorId, java.sql.Date appointmentDate, AppointmentStatus status);

    List<Appointment> findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber, java.sql.Date date, List<AppointmentStatus> statuses);

    long countByAppointmentDate(java.sql.Date date);

    long countByStatus(AppointmentStatus status);

    @Query("""
        SELECT ap.status, COUNT(ap) FROM Appointment ap
        GROUP BY ap.status
        """)
    List<Object[]> countByStatus();

    @Query("""
        SELECT off.name, off.officeNumber, COUNT(ap) FROM Appointment ap
        JOIN ap.doctor doc
        JOIN doc.office off
        GROUP BY off.name, off.officeNumber
        ORDER BY COUNT(ap) DESC
        """)
    List<Object[]> findBusiestOffice();

    @Modifying
    @Query("""
        UPDATE Appointment ap SET ap.status = :cancelled
        WHERE ap.doctor.id = :doctorId
        AND ap.appointmentDate = :date
        AND ap.status <> :done
        """)
    int cancelDay(@Param("doctorId") UUID doctorId, @Param("date") java.sql.Date date,
            @Param("cancelled") AppointmentStatus cancelled, @Param("done") AppointmentStatus done);

    @Modifying
    @Query("""
        DELETE FROM Appointment ap
        WHERE ap.status = :cancelled
        AND ap.appointmentDate < :date
        """)
    int deleteCancelledBefore(@Param("date") java.sql.Date date,
            @Param("cancelled") AppointmentStatus cancelled);
}
