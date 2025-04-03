package com.medical.homevisits.appointments.emergency.entity;

import com.medical.homevisits.appointments.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private String description;
    private boolean resolved;

    public void setPatientId(UUID patientId) {
        if (this.patient == null) {
            
            this.patient = new Patient();
        }
        this.patient.setID(patientId);
    }
}
