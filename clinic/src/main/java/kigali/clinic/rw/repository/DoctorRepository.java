package kigali.clinic.rw.repository;

import java.util.Optional;
import java.util.UUID;

import javax.print.Doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Doctor;


@Repository 
public interface DoctorRepository extends JpaRepository<Doctor,UUID> {
    
}
