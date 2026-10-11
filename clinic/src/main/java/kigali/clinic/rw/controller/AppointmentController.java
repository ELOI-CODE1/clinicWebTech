package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping(value = "/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAppointment(@RequestBody Appointment appointment) {
        Appointment savedAppointment = appointmentService.saveAppointment(appointment);
        return new ResponseEntity<>(savedAppointment, HttpStatus.CREATED);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getByStatus(@RequestParam AppointmentStatus status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/between", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);
        java.sql.Date d1 = java.sql.Date.valueOf(s);
        java.sql.Date d2 = java.sql.Date.valueOf(e);
        List<Appointment> appointments = appointmentService.getAppointmentsBetween(d1, d2);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping(value = "/stats/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> getStats() {
        List<Object[]> stats = appointmentService.getStatsByStatus();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PatchMapping(value = "/cancel-day")
    public ResponseEntity<String> cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        LocalDate d = LocalDate.parse(date);
        java.sql.Date sqlDate = java.sql.Date.valueOf(d);
        String message = appointmentService.cancelDay(doctorId, sqlDate);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Appointment>> getPage(@RequestParam int page, @RequestParam int size,
            @RequestParam String sort) {
        String[] parts = sort.split(",");
        String prop = parts[0];
        String dir = "asc";
        if (parts.length > 1) {
            dir = parts[1];
        }
        Sort s;
        if (dir.equalsIgnoreCase("desc")) {
            s = Sort.by(Sort.Direction.DESC, prop);
        } else {
            s = Sort.by(Sort.Direction.ASC, prop);
        }
        Pageable pageable = PageRequest.of(page, size, s);
        Page<Appointment> result = appointmentService.getAppointmentsPage(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cancelled-before")
    public ResponseEntity<String> deleteBefore(@RequestParam String date) {
        LocalDate d = LocalDate.parse(date);
        java.sql.Date sqlDate = java.sql.Date.valueOf(d);
        String message = appointmentService.deleteCancelledBefore(sqlDate);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable UUID id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @GetMapping(value = "/by-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(@RequestParam Date date) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Appointment> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointmentDetails) {
        Appointment updatedAppointment = appointmentService.updateAppointment(id, appointmentDetails);
        return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
        return new ResponseEntity<>("Appointment deleted successfully", HttpStatus.OK);
    }
}
