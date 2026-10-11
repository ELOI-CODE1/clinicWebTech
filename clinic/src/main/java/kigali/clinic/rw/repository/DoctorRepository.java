package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findByFirstName(String firstName);

    List<Doctor> findByLastName(String lastName);

    Optional<Doctor> findByOffice(Doctor doctor);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    @Query("""
        SELECT doc FROM Doctor doc
        JOIN doc.specializations spec
        WHERE LOWER(spec.name) = LOWER(:name)
        """)
    List<Doctor> findBySpecializationName(@Param("name") String name);

    @Query("""
        SELECT doc FROM Doctor doc
        WHERE doc.office IS NULL
        ORDER BY doc.lastName ASC
        """)
    List<Doctor> findDoctorsWithoutOffice();
    
}
