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

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping(value = "/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        String result = doctorService.saveDoctor(doctor);
        if (result.startsWith("All offices are taken")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping(value = "/by-specialization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> getBySpecialization(@RequestParam String name) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(name);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping(value = "/without-office", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> getWithoutOffice() {
        List<Doctor> doctors = doctorService.getDoctorsWithoutOffice();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Doctor> getDoctorById(@PathVariable UUID id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> searchDoctors(@RequestParam String firstName) {
        List<Doctor> doctors = doctorService.getDoctorsByFirstName(firstName);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Doctor> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctorDetails) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctorDetails);
        return new ResponseEntity<>(updatedDoctor, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return new ResponseEntity<>("Doctor deleted successfully", HttpStatus.OK);
    }
}
