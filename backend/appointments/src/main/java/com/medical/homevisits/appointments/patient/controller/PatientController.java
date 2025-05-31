package com.medical.homevisits.appointments.patient.controller;

import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;
import com.medical.homevisits.appointments.patient.entity.Patient;
import com.medical.homevisits.appointments.patient.repository.PatientRepository;
import com.medical.homevisits.appointments.patient.service.PatientService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PatientController {
    private final PatientService patientService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public PatientController(PatientService patientService, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.patientService = patientService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostMapping("/api/patients")
    public void createPatient(@RequestBody createRequest request){
        Patient patient = new Patient(request.getId(), request.getEmail(), request.getPhoneNumber(), request.getFirstName(), request.getLastName());
        patientService.create(patient);
    }

    /**
     * function for doctors to browse patients list
     * @param token  - doctor token
     * @return - patients list
     */
    @GetMapping("/api/patients")
    public ResponseEntity<List<Patient>> getPatients(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(required = false) String preferredEmail,
            @RequestParam(required = false) String preferredFirstName,
            @RequestParam(required = false) String preferredLastName
    ){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class);
        UUID doctorId = UUID.fromString(claims.get("id", String.class));

        if (!Objects.equals(role, "Doctor") || doctorRepository.findById(doctorId).isEmpty()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        List<Patient> patients = patientRepository.findAll();
        List<Patient> filteredList = new ArrayList<>();

        patients.forEach(patient -> {
            if ((preferredEmail == null || Objects.equals(patient.getEmail(), preferredEmail)) &&
                    (preferredFirstName == null || Objects.equals(patient.getFirstName(), preferredFirstName)) &&
                    (preferredLastName == null || Objects.equals(patient.getLastName(), preferredLastName))
            ){
                filteredList.add(patient);
            }
        });
        return ResponseEntity.ok(filteredList);
    }

}

@Getter
class   createRequest{
    private UUID id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
}