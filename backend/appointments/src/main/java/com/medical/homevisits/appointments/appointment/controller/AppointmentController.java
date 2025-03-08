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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Jwts;

import java.time.*;
import java.util.*;

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
        List<Appointment> appointments = service.getAppointments(AppointmentStatus.AVAILABLE, doctorId, appointmentDate, null);
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

        List<Appointment> appointments = service.getAppointments(status, doctorId, appointmentDate, null);
        return ResponseEntity.ok(appointments);
    }

    /**
     * function for patients to get their appointments (can view all appointments for specific patient)
     * @param status - appointment status
     * @param appointmentDate - date of appointment
     * @return - list of appointments that fit specification
     */
    @GetMapping("/patients")
    public ResponseEntity<List<Appointment>> getPatientAppointments(
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

        UUID patientId = UUID.fromString(claims.get("id", String.class));

        List<Appointment> appointments = service.getAppointments(status, null, appointmentDate, patientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * this function enables patient to register for given appointment
     * @param token
     */
    @PostMapping("/register")
    public void registerPatientOnAppointment(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody RegisterRequest request){

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID patientId = UUID.fromString(claims.get("id", String.class));

        if (service.find(request.getAppointmentId()).getStatus() != AppointmentStatus.AVAILABLE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "appointment not available");
        }
        service.registerPatient(request.getAppointmentId(), patientId);
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

    /**
     * function returns all available doctors - currently only their ids TODO: think about not returning doctorId
     * @param appointmentDate
     * @param preferredSpecialization - filtering doctors based on their specialization
     * @return
     */
    @GetMapping("/doctors/available")
    public ResponseEntity<Set<Doctor>> getAvailableDoctors(
            @RequestParam(required = false) LocalDate appointmentDate,
            @RequestParam(required = false) String preferredSpecialization
    ){
        Set<Doctor> doctorSet = new HashSet<>();
        List<Appointment> appointments = service.getAppointments(AppointmentStatus.AVAILABLE, null, appointmentDate, null);
        appointments.forEach((appointment -> {
            // adding doctor to set if his specialization matches preferred one
            if (preferredSpecialization == null || Objects.equals(appointment.getDoctor().getSpecialization(), preferredSpecialization)){
                doctorSet.add(appointment.getDoctor());
            }
        }));
        return ResponseEntity.ok(doctorSet);
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

@Getter
class RegisterRequest{
    private UUID appointmentId;
}