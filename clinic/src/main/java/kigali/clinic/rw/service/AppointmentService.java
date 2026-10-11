package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public String saveAppointment(Appointment appointment) {
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        Patient fetchedPatient = null;
        Doctor fetchedDoctor = null;
        if (patient != null && patient.getId() != null) {
            Optional<Patient> found = patientRepo.findById(patient.getId());
            if (found.isEmpty()) {
                return "Patient not found with id: " + patient.getId();
            }
            fetchedPatient = found.get();
        } else {
            return "Patient is required";
        }
        if (doctor != null && doctor.getId() != null) {
            Optional<Doctor> found = doctorRepo.findById(doctor.getId());
            if (found.isEmpty()) {
                return "Doctor not found with id: " + doctor.getId();
            }
            fetchedDoctor = found.get();
        } else {
            return "Doctor is required";
        }
        appointment.setPatient(fetchedPatient);
        appointment.setDoctor(fetchedDoctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        if (appointment.getAppointmentDate() != null) {
            if (appointment.getAppointmentDate().toLocalDate().isBefore(LocalDate.now())) {
                return "Appointment date cannot be in the past";
            }
            java.sql.Date d = new java.sql.Date(appointment.getAppointmentDate().getTime());
            boolean booked = appointmentRepo.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                    fetchedDoctor.getId(), d, AppointmentStatus.CANCELLED);
            if (booked) {
                return "Doctor is already booked on that date";
            }
        }
        appointmentRepo.save(appointment);
        return "Appointment saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public Appointment getAppointmentById(UUID id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found with id: " + id));
    }

    public Appointment updateAppointment(UUID id, Appointment appointmentDetails) {
        Appointment appointment = getAppointmentById(id);
        appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
        appointment.setReason(appointmentDetails.getReason());
        appointment.setPatient(appointmentDetails.getPatient());
        appointment.setDoctor(appointmentDetails.getDoctor());
        return appointmentRepo.save(appointment);
    }

    public String updateStatus(UUID id, AppointmentStatus status) {
        Optional<Appointment> found = appointmentRepo.findById(id);
        if (found.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = found.get();
        AppointmentStatus current = appointment.getStatus();
        boolean ok = false;
        if (current == AppointmentStatus.SCHEDULED
                && (status == AppointmentStatus.CONFIRMED || status == AppointmentStatus.CANCELLED)) {
            ok = true;
        }
        if (current == AppointmentStatus.CONFIRMED
                && (status == AppointmentStatus.COMPLETED || status == AppointmentStatus.CANCELLED)) {
            ok = true;
        }
        if (!ok) {
            return "Cannot change status from " + current + " to " + status;
        }
        appointment.setStatus(status);
        appointmentRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    public String reschedule(UUID id, java.sql.Date newDate) {
        Optional<Appointment> found = appointmentRepo.findById(id);
        if (found.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = found.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return "A COMPLETED appointment cannot be rescheduled";
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return "A CANCELLED appointment cannot be rescheduled";
        }
        if (newDate.toLocalDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        java.sql.Date current = appointment.getAppointmentDate();
        boolean sameDay = current != null && current.toLocalDate().equals(newDate.toLocalDate());
        if (!sameDay && appointment.getDoctor() != null) {
            boolean booked = appointmentRepo.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                    appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED);
            if (booked) {
                return "Doctor is already booked on that date";
            }
        }
        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }

    public void deleteAppointment(UUID id) {
        Appointment appointment = getAppointmentById(id);
        appointmentRepo.delete(appointment);
    }

    public List<Appointment> getAppointmentsByDate(Date date) {
        return appointmentRepo.findByAppointmentDate(date);
    }

    public Optional<Appointment> getAppointmentByReasonAndDate(String reason, Date date) {
        return appointmentRepo.findByReasonAndAppointmentDate(reason, date);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(java.sql.Date start, java.sql.Date end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> getStatsByStatus() {
        List<Object[]> rows = appointmentRepo.countByStatus();
        return rows;
    }

    @Transactional
    public String cancelDay(UUID doctorId, java.sql.Date date) {
        int n = appointmentRepo.cancelDay(doctorId, date, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return n + " appointments cancelled";
    }

    public Page<Appointment> getAppointmentsPage(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(java.sql.Date date) {
        int n = appointmentRepo.deleteCancelledBefore(date, AppointmentStatus.CANCELLED);
        return n + " appointments deleted";
    }
}
