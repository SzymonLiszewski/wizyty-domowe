package com.medical.homevisits.appointments.appointment.controller;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import com.medical.homevisits.appointments.appointment.entity.AppointmentStatus;
import com.medical.homevisits.appointments.appointment.service.AppointmentService;
import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;
import com.medical.homevisits.appointments.nurse.entity.Nurse;
import com.medical.homevisits.appointments.nurse.repository.NurseRepository;
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
    public void addAppointment(
            @RequestBody AddAppointmentRequest request,
            @RequestHeader(value = "Authorization") String token
    ){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class);

        if (!Objects.equals(role, "Doctor") && !Objects.equals(role, "Nurse")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "");
        }

        Doctor doctor = null;
        Nurse nurse = null;
        if (request.getDoctor() == null && request.getNurse() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor or nurse is required");
        }
        if (request.getDoctor() != null){
            doctor = doctorRepository.findById(request.getDoctor()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor with given ID does not exist"));
        }
        if (request.getNurse() != null) {
            nurse = nurseRepository.findById(request.getNurse()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse with given ID does not exist"));
        }
        if (request.getPatient() != null){
            Patient patient = patientRepository.findById(request.getPatient()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient with given ID does not exist"));
            service.create(Appointment.builder()
                    .patient(patient)
                    .appointmentStartTime(request.getAppointmentStartTime())
                    .appointmentEndTime(request.getAppointmentEndTime())
                    .address(request.getAddress())
                    .status(request.getStatus())
                    .doctor(doctor)
                    .nurse(nurse)
                    .notes(request.getNotes())
                    .build());
        }else {
            service.create(Appointment.builder()
                    .appointmentStartTime(request.getAppointmentStartTime())
                    .appointmentEndTime(request.getAppointmentEndTime())
                    .address(request.getAddress())
                    .status(request.getStatus())
                    .doctor(doctor)
                    .nurse(nurse)
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
            @RequestParam(required = false) LocalDate appointmentDate,
            @RequestParam(required = false) String city
            //TODO: add nurse in params
            ){
        List<Appointment> appointments = service.getAppointments(AppointmentStatus.AVAILABLE, doctorId, appointmentDate, null, city, null);
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

        List<Appointment> appointments = service.getAppointments(status, doctorId, appointmentDate, null, null, null);
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

        List<Appointment> appointments = service.getAppointments(status, null, appointmentDate, patientId, null, null);
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

        if (Objects.equals(claims.get("role", String.class), "Doctor")){
            UUID doctorId = UUID.fromString(claims.get("id", String.class));
            service.createAvailableAppoitnments(doctorId, null, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), request.getAppointmentsDuration());
            return;
        } else if (Objects.equals(claims.get("role", String.class), "Nurse")) {
            UUID nurseId = UUID.fromString(claims.get("id", String.class));
            service.createAvailableAppoitnments(null,nurseId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), request.getAppointmentsDuration());
            return;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authorized for this action");
    }

    /**
     * function returns all available doctors
     * TODO: change way of finding doctors (create 'isAvailable' field in doctor class?)
     * @param appointmentDate
     * @param preferredSpecialization - filtering doctors based on their specialization
     * @return
     */
    @GetMapping("/doctors/available")
    public ResponseEntity<Set<Doctor>> getAvailableDoctors(
            @RequestParam(required = false) LocalDate appointmentDate,
            @RequestParam(required = false) String preferredSpecialization,
            @RequestParam(required = false) String preferredCity,
            @RequestParam(required = false) String preferredFirstName,
            @RequestParam(required = false) String preferredLastName
    ){
        Set<Doctor> doctorSet = new HashSet<>();
        List<Appointment> appointments = service.getAppointments(AppointmentStatus.AVAILABLE, null, appointmentDate, null, preferredCity, null);
        appointments.forEach((appointment -> {
            //TODO: implement finding doctor in separate function

            // adding doctor to set if his specialization matches preferred one
            if ((preferredSpecialization == null || Objects.equals(appointment.getDoctor().getSpecialization(), preferredSpecialization)) &&
                    (preferredCity == null || Objects.equals(appointment.getDoctor().getWorkPlace().getCity(), preferredCity)) &&
                    (preferredFirstName == null || Objects.equals(appointment.getDoctor().getFirstName(), preferredFirstName)) &&
                    (preferredLastName == null || Objects.equals(appointment.getDoctor().getLastName(), preferredLastName))){
                doctorSet.add(appointment.getDoctor());
            }
        }));
        return ResponseEntity.ok(doctorSet);
    }
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId) {
        service.delete(appointmentId);
        return ResponseEntity.noContent().build();
    }
    @Autowired
    private NurseRepository nurseRepository;

    @GetMapping("/nurses")
    public ResponseEntity<List<Appointment>> getNurseAppointments(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) LocalDate appointmentDate
    ) {
        String jwt = token.replace("Bearer ", "");
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
        UUID nurseId = UUID.fromString(claims.get("id", String.class));

        List<Appointment> appointments = service.getAppointments(status, null, appointmentDate, null, null,nurseId);
        return ResponseEntity.ok(appointments);
    }


    @GetMapping("/nurses/available")
    public ResponseEntity<Set<Nurse>> getAvailableNurses(
            @RequestParam(required = false) LocalDate appointmentDate
    ) {
        Set<Nurse> nurseSet = new HashSet<>();
        List<Appointment> appointments = service.getAppointments(AppointmentStatus.AVAILABLE, null, appointmentDate, null, null, null);
        appointments.forEach((appointment -> {
            if (appointment.getNurse() != null) {
                nurseSet.add(appointment.getNurse());
            }
        }));
        return ResponseEntity.ok(nurseSet);
    }
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "");
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();

        UUID userId = UUID.fromString(claims.get("id", String.class));
        String role = claims.get("role", String.class);

        Appointment appointment = service.find(id);
        if (role.equals("Doctor") && appointment.getDoctor().getID().equals(userId) ||
            role.equals("Nurse") && appointment.getNurse().getID().equals(userId)) {
            appointment.setStatus(AppointmentStatus.CANCELED);
            return ResponseEntity.noContent().build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to cancel this appointment");
    }


}

@Getter
class AddAppointmentRequest{
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status; //status of the appointment (reserved, available, cancelled or completed)
    private LocalDateTime appointmentStartTime;
    private LocalDateTime appointmentEndTime;
    private UUID doctor;
    private UUID nurse;
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
