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

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;

@RestController
@RequestMapping(value = "/api/specializations")
public class SpecializationController {

    @Autowired
    private SpecializationService specializationService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveSpecialization(@RequestBody Specialization specialization) {
        Specialization savedSpecialization = specializationService.saveSpecialization(specialization);
        return new ResponseEntity<>(savedSpecialization, HttpStatus.CREATED);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        List<Specialization> specializations = specializationService.getAllSpecializations();
        return new ResponseEntity<>(specializations, HttpStatus.OK);
    }

    @GetMapping(value = "/unused", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Specialization>> getUnused() {
        List<Specialization> specializations = specializationService.getUnusedSpecializations();
        return new ResponseEntity<>(specializations, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Specialization> getSpecializationById(@PathVariable UUID id) {
        Specialization specialization = specializationService.getSpecializationById(id);
        return new ResponseEntity<>(specialization, HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Specialization>> searchSpecializations(@RequestParam String name) {
        List<Specialization> specializations = specializationService.getSpecializationsByName(name);
        return new ResponseEntity<>(specializations, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Specialization> updateSpecialization(@PathVariable UUID id, @RequestBody Specialization specializationDetails) {
        Specialization updatedSpecialization = specializationService.updateSpecialization(id, specializationDetails);
        return new ResponseEntity<>(updatedSpecialization, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteSpecialization(@PathVariable UUID id) {
        specializationService.deleteSpecialization(id);
        return new ResponseEntity<>("Specialization deleted successfully", HttpStatus.OK);
    }
}
