package com.medical.homevisits.appointments.emergency.controller;

import com.medical.homevisits.appointments.emergency.entity.EmergencyReport;
import com.medical.homevisits.appointments.emergency.service.EmergencyReportService;
import com.medical.homevisits.appointments.patient.entity.Patient;
import com.medical.homevisits.appointments.patient.repository.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyReportController {

    private final EmergencyReportService service;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    public EmergencyReportController(EmergencyReportService service, PatientRepository patientRepository) {
        this.service = service;
        this.patientRepository = patientRepository;
    }

    /**
     * Function to create a new emergency report
     * @param token - Authorization token containing patient ID
     * @param report - Emergency report data
     * @return - Response indicating whether the report was created successfully
     */
    @PostMapping("")
    public ResponseEntity<String> addEmergencyReport(@RequestHeader(value = "Authorization") String token,
                                                     @RequestBody EmergencyReport report) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            String patientId = claims.get("id", String.class);
            if (patientId != null) {
                Patient patient = patientRepository.findById(UUID.fromString(patientId))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient with given ID does not exist"));

                
                report.setPatient(patient);
                service.create(report); 

                return ResponseEntity.ok("Emergency report created successfully.");
            } else {
                return ResponseEntity.badRequest().body("Patient ID not found in token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error parsing token: " + e.getMessage());
        }
    }

    /**
     * Function for patients to get their emergency reports
     * @param token - Authorization token
     * @return - List of emergency reports for a patient
     */
    @GetMapping("/patients")
    public ResponseEntity<Optional<EmergencyReport>> getPatientEmergencyReports(@RequestHeader(value = "Authorization") String token) {
        // Extracting patient ID from token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID patientId = UUID.fromString(claims.get("id", String.class));
        Optional<EmergencyReport> reports = service.getReportsByPatient(patientId);
        return ResponseEntity.ok(reports);
    }

    /**
     * Function to get all emergency reports (for admins or authorized users)
     * @return - List of all emergency reports
     */
    @GetMapping("")
    public ResponseEntity<List<EmergencyReport>> getAllEmergencyReports() {
        List<EmergencyReport> reports = service.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * Function for patients to get details of a specific emergency report
     * @param reportId - ID of the emergency report
     * @param token - Authorization token
     * @return - Details of a specific emergency report
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<EmergencyReport> getEmergencyReportDetails(@PathVariable UUID reportId,
                                                                     @RequestHeader(value = "Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID patientId = UUID.fromString(claims.get("id", String.class));
        EmergencyReport report = service.getReportById(reportId);

        if (report.getPatient().getId().equals(patientId)) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}

