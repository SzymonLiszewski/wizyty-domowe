package com.medical.homevisits.appointments.emergency.entity;

import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.paramedic.entity.Paramedic;
import com.medical.homevisits.appointments.patient.entity.Patient;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class EmergencyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name="paramedic", nullable = true)
    private Paramedic paramedic;
    private String address;
    private LocalDateTime EmergencyReportTime;
    private String description;
    private EmergencyStatus status; //status of the appointment (Available, In_progress, Completed)

    public void setPatientId(UUID patientId) {
        if (this.patient == null) {
            
            this.patient = new Patient();
        }
        this.patient.setID(patientId);
    }
}
