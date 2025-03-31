package com.medical.homevisits.appointments.prescription.repository;

import com.medical.homevisits.appointments.prescription.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
	 List<Prescription> findByPatientId(UUID patientId);
	List<Prescription> findByDoctorId(UUID doctorId);

}
