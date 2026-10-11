package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.PatientService;

@RestController
@RequestMapping(value = "/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePatient(@RequestBody Patient patient) {
        Patient savedPatient = patientService.savePatient(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping(value = "/by-last-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getByLastName(@RequestParam String lastName) {
        List<Patient> patients = patientService.getPatientsByLastNameIgnoreCase(lastName);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping(value = "/of-doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getOfDoctor(@PathVariable UUID doctorId) {
        List<Patient> patients = patientService.getPatientsOfDoctor(doctorId);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping(value = "/frequent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getFrequent(@RequestParam int min) {
        List<Patient> patients = patientService.getFrequentPatients(min);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Patient> getPatientById(@PathVariable UUID id) {
        Patient patient = patientService.getPatientById(id);
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String firstName, @RequestParam String lastName) {
        List<Patient> patients = patientService.getPatientsByFirstName(firstName);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Patient> updatePatient(@PathVariable UUID id, @RequestBody Patient patientDetails) {
        Patient updatedPatient = patientService.updatePatient(id, patientDetails);
        return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return new ResponseEntity<>("Patient deleted successfully", HttpStatus.OK);
    }
}
