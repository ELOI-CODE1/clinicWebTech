package kigali.clinic.rw.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity 
public class Specialization {

    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
     private UUID id;

     @Column (name="name")
     private String name;

     public UUID getId() {
         return id;
     }

     public void setId(UUID id) {
         this.id = id;
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public List<Doctor> getDoctors() {
         return doctors;
     }

     public void setDoctors(List<Doctor> doctors) {
         this.doctors = doctors;
     }

@ManyToMany (mappedBy = "specializations")
    @JsonIgnore
      private List<Doctor> doctors = new ArrayList<>();

}