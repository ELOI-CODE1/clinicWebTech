package kigali.clinic.rw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import kigali.clinic.rw.domain.Appointment;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository 
public interface AppointmentRepository extends JpaRepository<Appointment,UUID>{
    List<Appointment> findByDate(Date appointment_date);

    Optional<Appointment> findByReasonAndByAppointmentDate(String reason, Date appointmenDate);
}
