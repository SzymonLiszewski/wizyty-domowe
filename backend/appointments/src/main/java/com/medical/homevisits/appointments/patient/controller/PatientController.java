package com.medical.homevisits.appointments.patient.controller;

import com.medical.homevisits.appointments.patient.entity.Patient;
import com.medical.homevisits.appointments.patient.service.PatientService;
import lombok.Getter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/api/patients")
    public void createPatient(@RequestBody createRequest request){
        Patient patient = new Patient(request.getId(), request.getEmail(), request.getPhoneNumber(), request.getFirstName(), request.getLastName());
        patientService.create(patient);
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