package com.medical.homevisits.appointments.prescription.controller;

import com.medical.homevisits.appointments.prescription.entity.Prescription;
import com.medical.homevisits.appointments.prescription.service.PrescriptionService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Jwts;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService service;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    public PrescriptionController(PrescriptionService service, PatientRepository patientRepository, DoctorRepository doctorRepository, NurseRepository nurseRepository) {
        this.service = service;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
    }

    /**
     * Funkcja umożliwiająca lekarzowi wypisanie recepty dla pacjenta
     */
    @PostMapping("")
    public ResponseEntity<Void> createPrescription(@RequestHeader(value = "Authorization") String token, @RequestBody CreatePrescriptionRequest request) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID doctorId = UUID.fromString(claims.get("id", String.class));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor with given ID does not exist"));

        Patient patient = patientRepository.findById(request.getPatient())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient with given ID does not exist"));

        Prescription prescription = new Prescription();
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);
        prescription.setMedication(request.getMedication());
        prescription.setDosage(request.getDosage());
        prescription.setNotes(request.getNotes());
        prescription.setPrescriptionTime(request.getDate());
        service.createPrescription(prescription);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Funkcja umożliwiająca pacjentowi przeglądanie swoich recept
     */
    @GetMapping("/patients")
    public ResponseEntity<List<Prescription>> getPatientPrescriptions(
            @RequestHeader(value = "Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID patientId = UUID.fromString(claims.get("id", String.class));
        List<Prescription> prescriptions = service.getPrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    /**
     * Funkcja umożliwiająca lekarzowi przeglądanie swoich recept
     */
    @GetMapping("/doctors")
    public ResponseEntity<List<Prescription>> getDoctorPrescriptions(
            @RequestHeader(value = "Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID doctorId = UUID.fromString(claims.get("id", String.class));

        List<Prescription> prescriptions = service.getPrescriptionsByDoctor(doctorId);
        return ResponseEntity.ok(prescriptions);
    }

    /**
     * Funkcja umożliwiająca lekarzowi edytowanie istniejącej recepty
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePrescription(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable("id") UUID prescriptionId,
            @RequestBody UpdatePrescriptionRequest request) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID doctorId = UUID.fromString(claims.get("id", String.class));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor with given ID does not exist"));

        Prescription prescription = service.findPrescriptionById(prescriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found"));

        if (!prescription.getDoctor().getID().equals(doctorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this prescription");
        }

        prescription.setMedication(request.getMedication());
        prescription.setDosage(request.getDosage());
        prescription.setNotes(request.getNotes());

        service.updatePrescription(prescription);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/doctors/from-workplace")
    public ResponseEntity<List<Doctor>> getDoctorsFromSameHospital(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();

        UUID nurseId = UUID.fromString(claims.get("id", String.class));
        Nurse nurse = nurseRepository.findById(nurseId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found"));

        List<Doctor> doctors = doctorRepository.findByWorkPlace(nurse.getWorkPlace());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/nurses/from-workplace")
    public ResponseEntity<List<Nurse>> getNursesFromSameHospital(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();

        UUID doctorId = UUID.fromString(claims.get("id", String.class));
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "doctor not found"));

        List<Nurse> nurses = nurseRepository.findByWorkPlace(doctor.getWorkPlace());
        return ResponseEntity.ok(nurses);
    }

}

@Getter
class CreatePrescriptionRequest {
    private UUID patient; 
    private String medication;
    private String dosage;
    private String notes;
    private LocalDateTime date;
}

@Getter
class UpdatePrescriptionRequest {
    private String medication;
    private String dosage;
    private String notes;
}
