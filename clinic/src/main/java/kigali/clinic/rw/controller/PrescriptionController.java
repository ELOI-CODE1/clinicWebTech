package kigali.clinic.rw.controller;

import java.util.List;
import java.util.Optional;
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

import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping(value = "/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePrescription(@RequestParam UUID appointmentId,
            @RequestBody Prescription prescription) {
        String result = prescriptionService.savePrescription(appointmentId, prescription);
        if (result.equals("Prescription saved successfully")) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
        if (result.equals("The appointment with that id does not exist")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/appointment/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByAppointment(@PathVariable UUID appointmentId) {
        Optional<List<Prescription>> rows = prescriptionService.getByAppointment(appointmentId);
        if (rows.isEmpty()) {
            return new ResponseEntity<>("The appointment with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rows.get(), HttpStatus.OK);
    }

    @GetMapping(value = "/patient/{ssNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Prescription>> getByPatient(@PathVariable String ssNumber) {
        List<Prescription> rows = prescriptionService.getByPatientSsNumber(ssNumber);
        return new ResponseEntity<>(rows, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePrescription(@PathVariable UUID id,
            @RequestBody Prescription prescriptionDetails) {
        String result = prescriptionService.updatePrescription(id, prescriptionDetails);
        if (result.equals("Prescription updated successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deletePrescription(@PathVariable UUID id) {
        String result = prescriptionService.deletePrescription(id);
        if (result.equals("Prescription deleted successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }
}
