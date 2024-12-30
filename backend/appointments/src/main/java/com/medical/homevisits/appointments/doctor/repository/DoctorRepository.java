package com.medical.homevisits.appointments.doctor.repository;

import com.medical.homevisits.appointments.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
}
