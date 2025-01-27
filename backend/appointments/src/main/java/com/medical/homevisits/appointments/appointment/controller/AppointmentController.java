package com.medical.homevisits.appointments.appointment.controller;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import com.medical.homevisits.appointments.appointment.entity.AppointmentStatus;
import com.medical.homevisits.appointments.appointment.service.AppointmentService;
import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;
import com.medical.homevisits.appointments.patient.entity.Patient;
import com.medical.homevisits.appointments.patient.repository.PatientRepository;
import io.jsonwebtoken.Claims;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Jwts;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    public AppointmentController(AppointmentService service, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.service = service;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @PostMapping("")
    public void addAppointment(@RequestBody AddAppointmentRequest request){
        Doctor doctor = doctorRepository.findById(request.getDoctor()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor with given ID does not exist"));
        if (request.getPatient() != null){
            Patient patient = patientRepository.findById(request.getPatient()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient with given ID does not exist"));
            service.create(Appointment.builder()
                    .patient(patient)
                    .appointmentStartTime(request.getAppointmentStartTime())
                    .appointmentEndTime(request.getAppointmentEndTime())
                    .address(request.getAddress())
                    .status(request.getStatus())
                    .doctor(doctor)
                    .notes(request.getNotes())
                    .build());
        }else {
            service.create(Appointment.builder()
                    .appointmentStartTime(request.getAppointmentStartTime())
                    .appointmentEndTime(request.getAppointmentEndTime())
                    .address(request.getAddress())
                    .status(request.getStatus())
                    .doctor(doctor)
                    .notes(request.getNotes())
                    .build());
        }
    }

    /**
     * function for all users for viewing appointments (can view only available appointments)
     * @param doctorId - filtering available appointments by doctor
     * @param appointmentDate - filtering available appointments by date
     * @return - list of appointments that fit specification
     */
    @GetMapping("")
    public ResponseEntity<List<Appointment>> getAvailableAppointments(
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) LocalDate appointmentDate
            ){
        List<Appointment> appointments = service.getAppointments(AppointmentStatus.AVAILABLE, doctorId, appointmentDate);
        return ResponseEntity.ok(appointments);
    }

    /**
     * function for doctors to get their appointments (can view all appointments for specific doctor)
     * @param status - appointment status
     * @param appointmentDate - date of appointment
     * @return - list of appointments that fit specification
     */
    @GetMapping("/doctors")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) LocalDate appointmentDate
    ){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID doctorId = UUID.fromString(claims.get("id", String.class));

        List<Appointment> appointments = service.getAppointments(status, doctorId, appointmentDate);
        return ResponseEntity.ok(appointments);
    }

    /**
     * this function allows doctors to pass their available time and generate appointments calendar for 1 month forward
     */
    @PostMapping("/doctors")
    public void addAppointmentsCalendar(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody CalendarRequest request){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID doctorId = UUID.fromString(claims.get("id", String.class));
        service.createAvailableAppoitnments(doctorId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), request.getAppointmentsDuration());
    }

}

@Getter
class AddAppointmentRequest{
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status; //status of the appointment (reserved, available, cancelled or completed)
    private LocalDateTime appointmentStartTime;
    private LocalDateTime appointmentEndTime;
    private UUID doctor;
    private UUID patient;
    private String address;
    private String notes;
}

@Getter
class CalendarRequest{
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Duration appointmentsDuration;
}
