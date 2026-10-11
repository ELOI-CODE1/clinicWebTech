package kigali.clinic.rw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping (value = "/api/offices")
public class OfficeController {
  
    @Autowired 
    private OfficeService officeService;

    
     @PostMapping(value="/save", consumes = MediaType.APPLICATION_JSON_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE
     )
    public ResponseEntity<?> saveOffice(@RequestBody Office office){
        
      String returnedMessage =  officeService.saveOffice(office);
      if (returnedMessage.equalsIgnoreCase("saved successfully")) {
        return new ResponseEntity<>(returnedMessage,HttpStatus.CREATED );
      }
        return new ResponseEntity<>(returnedMessage,HttpStatus.CONFLICT );
    }
    @GetMapping (value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> getAllOffices(){
      List<Office> offices = officeService.getAllOffices();
      return new ResponseEntity<>(offices, HttpStatus.OK);
    }

    @GetMapping (value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> getFreeOffices(){
      List<Office> offices = officeService.getFreeOffices();
      return new ResponseEntity<>(offices, HttpStatus.OK);
    }

    @GetMapping (value = "/busiest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBusiest(){
      Object result = officeService.getBusiestOffice();
      return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
