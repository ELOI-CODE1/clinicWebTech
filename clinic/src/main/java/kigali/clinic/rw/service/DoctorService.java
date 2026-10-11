package kigali.clinic.rw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private OfficeRepository officeRepo;
    @Autowired
    private SpecializationRepository specializationRepo;

    @Transactional
    public String saveDoctor(Doctor doctor) {
        if (doctorRepo.existsByFirstNameAndLastName(doctor.getFirstName(), doctor.getLastName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Doctor already exists with this name");
        }

        List<Specialization> specs = new ArrayList<>();
        if (doctor.getSpecializations() != null) {
            for (Specialization s : doctor.getSpecializations()) {
                if (s == null || s.getId() == null) {
                    continue;
                }
                Optional<Specialization> found = specializationRepo.findById(s.getId());
                if (found.isEmpty()) {
                    continue;
                }
                boolean dup = false;
                for (Specialization have : specs) {
                    if (have.getId().equals(s.getId())) {
                        dup = true;
                    }
                }
                if (!dup) {
                    specs.add(found.get());
                }
            }
        }

        Office asked = doctor.getOffice();
        Office toSet = null;
        if (asked != null && asked.getId() != null) {
            Optional<Office> found = officeRepo.findById(asked.getId());
            if (found.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found with id: " + asked.getId());
            }
            toSet = found.get();
        } else if (asked == null) {
            List<Doctor> all = doctorRepo.findAll();
            List<Office> offices = officeRepo.findAll();
            for (Office o : offices) {
                boolean taken = false;
                for (Doctor d : all) {
                    if (d.getOffice() != null && d.getOffice().getId().equals(o.getId())) {
                        taken = true;
                    }
                }
                if (!taken) {
                    toSet = o;
                    break;
                }
            }
        }

        doctor.setOffice(toSet);
        doctor.setSpecializations(specs);
        doctorRepo.save(doctor);

        if (toSet != null) {
            return "Doctor saved successfully with office ID: " + toSet.getId();
        }
        return "Doctor saved successfully with no office";
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Doctor getDoctorById(UUID id) {
        return doctorRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id: " + id));
    }

    @Transactional
    public Doctor updateDoctor(UUID id, Doctor doctorDetails) {
        Doctor doctor = getDoctorById(id);
        doctor.setFirstName(doctorDetails.getFirstName());
        doctor.setLastName(doctorDetails.getLastName());
        doctor.setDateOfBirth(doctorDetails.getDateOfBirth());
        doctor.setOffice(doctorDetails.getOffice());
        if (doctorDetails.getSpecializations() != null) {
            List<Specialization> newSpecs = new ArrayList<>();
            for (Specialization s : doctorDetails.getSpecializations()) {
                if (s == null || s.getId() == null) {
                    continue;
                }
                Optional<Specialization> found = specializationRepo.findById(s.getId());
                if (found.isEmpty()) {
                    continue;
                }
                boolean dup = false;
                for (Specialization have : newSpecs) {
                    if (have.getId().equals(s.getId())) {
                        dup = true;
                    }
                }
                if (!dup) {
                    newSpecs.add(found.get());
                }
            }
            doctor.setSpecializations(newSpecs);
        }
        return doctorRepo.save(doctor);
    }

    public void deleteDoctor(UUID id) {
        Doctor doctor = getDoctorById(id);
        doctorRepo.delete(doctor);
    }

    public List<Doctor> getDoctorsByFirstName(String firstName) {
        return doctorRepo.findByFirstName(firstName);
    }

    public List<Doctor> getDoctorsByLastName(String lastName) {
        return doctorRepo.findByLastName(lastName);
    }

    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepo.findBySpecializationName(name);
    }

    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }

    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepo.searchByName(name);
    }
}
