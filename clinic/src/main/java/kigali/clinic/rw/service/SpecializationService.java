package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specializationRepo;

    public Specialization saveSpecialization(Specialization specialization) {
        if (specializationRepo.existsByName(specialization.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Specialization already exists");
        }
        return specializationRepo.save(specialization);
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepo.findAll();
    }

    public Specialization getSpecializationById(UUID id) {
        return specializationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialization not found with id: " + id));
    }

    public Specialization updateSpecialization(UUID id, Specialization specializationDetails) {
        Specialization specialization = getSpecializationById(id);
        specialization.setName(specializationDetails.getName());
        return specializationRepo.save(specialization);
    }

    public void deleteSpecialization(UUID id) {
        Specialization specialization = getSpecializationById(id);
        specializationRepo.delete(specialization);
    }

    public List<Specialization> getSpecializationsByName(String name) {
        return specializationRepo.findByName(name);
    }

    public List<Specialization> getUnusedSpecializations() {
        return specializationRepo.findUnusedSpecializations();
    }
}
