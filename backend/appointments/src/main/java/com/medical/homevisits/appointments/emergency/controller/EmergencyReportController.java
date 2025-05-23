package com.medical.homevisits.appointments.emergency.controller;

import com.medical.homevisits.appointments.emergency.entity.EmergencyReport;
import com.medical.homevisits.appointments.emergency.entity.EmergencyStatus;
import com.medical.homevisits.appointments.emergency.repository.EmergencyReportRepository;
import com.medical.homevisits.appointments.emergency.service.EmergencyReportService;
import com.medical.homevisits.appointments.paramedic.entity.Paramedic;
import com.medical.homevisits.appointments.paramedic.repository.ParamedicRepository;
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

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyReportController {

    private final EmergencyReportService service;
    private final PatientRepository patientRepository;
    private final ParamedicRepository paramedicRepository;
    private final EmergencyReportRepository emergencyReportRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    public EmergencyReportController(EmergencyReportService service, PatientRepository patientRepository, ParamedicRepository paramedicRepository, EmergencyReportRepository emergencyReportRepository) {
        this.service = service;
        this.patientRepository = patientRepository;
        this.paramedicRepository=paramedicRepository;
        this.emergencyReportRepository = emergencyReportRepository;
    }

    /**
     * Function to create a new emergency report
     * @param token - Authorization token containing patient ID
     * @param report - Emergency report data
     * @return - Response indicating whether the report was created successfully
     */
    @PostMapping("")
    public ResponseEntity<String> addEmergencyReport(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody EmergencyReport report
    ) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            String patientId = claims.get("id", String.class);
            if (patientId == null) {
                return ResponseEntity.badRequest().body("Patient ID not found in token.");
            }

            Patient patient = patientRepository.findById(UUID.fromString(patientId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

            report.setPatient(patient);

            if (report.getEmergencyReportTime() == null) {
                report.setEmergencyReportTime(LocalDateTime.now());
            }

            report.setStatus(EmergencyStatus.Available);
            report.setParamedic(null);;
            

            service.create(report);
            return ResponseEntity.ok("Emergency report created successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
        }
    }
    /**
     * Function for patients to get their emergency reports
     * @param token - Authorization token
     * @return - List of emergency reports for a patient
     */
    @GetMapping("/patients")
    public ResponseEntity<List<EmergencyReport>> getPatientEmergencyReports(@RequestHeader(value = "Authorization") String token) {
        // Extracting patient ID from token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID patientId = UUID.fromString(claims.get("id", String.class));
        List<EmergencyReport> reports = service.getReportsByPatient(patientId);
        return ResponseEntity.ok(reports);
    }

    /**
     * Function for patients to get their emergency reports
     * @param token - Authorization token
     * @return - List of emergency reports for a patient
     */
    @GetMapping("/paramedics")
    public ResponseEntity<List<EmergencyReport>> getParamedicEmergencyReports(@RequestHeader(value = "Authorization") String token) {
        // Extracting ID from token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID paramedicId = UUID.fromString(claims.get("id", String.class));
        List<EmergencyReport> reports = service.getReportsByParamedic(paramedicId);
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

        if (report.getPatient().getID().equals(patientId)) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    @PutMapping("/{reportId}/assign")
    public ResponseEntity<String> assignParamedicToReport(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID reportId
    ) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID paramedicId = UUID.fromString(claims.get("id", String.class));

        EmergencyReport report = service.getReportById(reportId);
        if (report == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Emergency report not found.");
        }

        if (report.getStatus() != EmergencyStatus.Available) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Emergency report is already assigned.");
        }

        Paramedic paramedic = paramedicRepository.findById(paramedicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paramedic not found"));

        report.setParamedic(paramedic);
        report.setStatus(EmergencyStatus.In_progress);

        service.update(report); 

        return ResponseEntity.ok("Report assigned to paramedic.");
    }
    @GetMapping("/available")
    public ResponseEntity<List<EmergencyReport>> getAvailableEmergencyReports() {
        List<EmergencyReport> reports = service.getReportsByStatus(EmergencyStatus.Available);
        return ResponseEntity.ok(reports);
    }

    /**
     * Function for paramedics to change status of appointment
     */
    @PutMapping("/{reportId}/status")
    public ResponseEntity<String> changeEmergencyStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID reportId,
            @RequestBody changeStatusRequest statusRequest
    ){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID paramedicId = UUID.fromString(claims.get("id", String.class));
        EmergencyReport emergencyReport = emergencyReportRepository.findById(reportId).get();

        //allowing assigned paramedics to change status
        if (emergencyReport.getParamedic().getID().equals(paramedicId)){
            emergencyReport.setStatus(statusRequest.getStatus());
            emergencyReportRepository.save(emergencyReport);
            return ResponseEntity.ok("Status changed succesfully");
        }
        return ResponseEntity.ok("User not allowed for this action");
    }
}

@Getter
class changeStatusRequest{
    private EmergencyStatus status;
}

