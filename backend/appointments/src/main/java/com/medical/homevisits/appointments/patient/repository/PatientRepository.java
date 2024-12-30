package com.medical.homevisits.appointments.patient.repository;

import com.medical.homevisits.appointments.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
