package kigali.clinic.rw.service;

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

    public Appointment saveAppointment(Appointment appointment) {
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        if (patient != null && patient.getId() != null) {
            Patient fetchedPatient = patientRepo.findById(patient.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id: " + patient.getId()));
            appointment.setPatient(fetchedPatient);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient is required");
        }
        if (doctor != null && doctor.getId() != null) {
            Doctor fetchedDoctor = doctorRepo.findById(doctor.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id: " + doctor.getId()));
            appointment.setDoctor(fetchedDoctor);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor is required");
        }
        if (appointment.getAppointmentDate() != null) {
            java.sql.Date d = new java.sql.Date(appointment.getAppointmentDate().getTime());
            boolean booked = appointmentRepo.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                    appointment.getDoctor().getId(), d, AppointmentStatus.CANCELLED);
            if (booked) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Doctor is already booked on that date");
            }
        }
        return appointmentRepo.save(appointment);
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
        appointment.setStatus(appointmentDetails.getStatus());
        appointment.setPatient(appointmentDetails.getPatient());
        appointment.setDoctor(appointmentDetails.getDoctor());
        return appointmentRepo.save(appointment);
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
