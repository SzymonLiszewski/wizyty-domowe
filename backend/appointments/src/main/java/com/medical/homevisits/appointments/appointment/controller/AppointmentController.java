package com.medical.homevisits.appointments.appointment.controller;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import com.medical.homevisits.appointments.appointment.entity.AppointmentStatus;
import com.medical.homevisits.appointments.appointment.service.AppointmentService;
import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;
import com.medical.homevisits.appointments.patient.entity.Patient;
import com.medical.homevisits.appointments.patient.repository.PatientRepository;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentController(AppointmentService service, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.service = service;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @PostMapping("/addAppointment")
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

    @GetMapping("/getAppointments")
    public ResponseEntity<List<Appointment>> getAvailableAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) LocalDateTime appointmentDate
            ){
        List<Appointment> appointments = service.getAppointments(status, doctorId, appointmentDate);
        return ResponseEntity.ok(appointments);
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

