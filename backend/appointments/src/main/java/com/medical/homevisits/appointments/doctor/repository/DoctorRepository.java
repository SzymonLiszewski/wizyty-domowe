package com.medical.homevisits.appointments.doctor.repository;

import com.medical.homevisits.appointments.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
	 List<Doctor> findByWorkplace(String workplace);
}

