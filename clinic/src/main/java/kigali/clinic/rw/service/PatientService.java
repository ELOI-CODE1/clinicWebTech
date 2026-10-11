package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public Patient savePatient(Patient patient) {
        if (patientRepo.existsByFirstNameAndLastName(patient.getFirstName(), patient.getLastName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Patient already exists with this name");
        }
        return patientRepo.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Patient getPatientById(UUID id) {
        return patientRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id: " + id));
    }

    public Patient updatePatient(UUID id, Patient patientDetails) {
        Patient patient = getPatientById(id);
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setSsNumber(patientDetails.getSsNumber());
        return patientRepo.save(patient);
    }

    public void deletePatient(UUID id) {
        Patient patient = getPatientById(id);
        patientRepo.delete(patient);
    }

    public List<Patient> getPatientsByFirstName(String firstName) {
        return patientRepo.findByFirstName(firstName);
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepo.findByLastName(lastName);
    }

    public List<Patient> getPatientsByLastNameIgnoreCase(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public List<Patient> getPatientsOfDoctor(UUID doctorId) {
        if (doctorRepo.findById(doctorId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The doctor with that id does not exist");
        }
        return patientRepo.findPatientsOfDoctor(doctorId);
    }

    public List<Patient> getFrequentPatients(int min) {
        return patientRepo.findFrequentPatients(min);
    }
}
