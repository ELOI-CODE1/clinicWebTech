package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> found = appointmentRepo.findById(appointmentId);
        if (found.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = found.get();
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }
        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment);
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    public Optional<List<Prescription>> getByAppointment(UUID appointmentId) {
        Optional<Appointment> found = appointmentRepo.findById(appointmentId);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        List<Prescription> rows = prescriptionRepo.findByAppointment(found.get());
        return Optional.of(rows);
    }

    public List<Prescription> getByPatientSsNumber(String ssNumber) {
        List<Prescription> rows = prescriptionRepo.findByPatientSsNumber(ssNumber);
        return rows;
    }

    public String updatePrescription(UUID id, Prescription prescriptionDetails) {
        Optional<Prescription> found = prescriptionRepo.findById(id);
        if (found.isEmpty()) {
            return "The prescription with that id does not exist";
        }
        Prescription prescription = found.get();
        prescription.setMedicineName(prescriptionDetails.getMedicineName());
        prescription.setDosage(prescriptionDetails.getDosage());
        prescription.setDurationDays(prescriptionDetails.getDurationDays());
        prescriptionRepo.save(prescription);
        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        Optional<Prescription> found = prescriptionRepo.findById(id);
        if (found.isEmpty()) {
            return "The prescription with that id does not exist";
        }
        prescriptionRepo.delete(found.get());
        return "Prescription deleted successfully";
    }
}
