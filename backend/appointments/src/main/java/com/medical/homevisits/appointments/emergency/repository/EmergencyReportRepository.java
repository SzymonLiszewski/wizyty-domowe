package com.medical.homevisits.appointments.emergency.repository;

import com.medical.homevisits.appointments.emergency.entity.EmergencyReport;
import com.medical.homevisits.appointments.emergency.entity.EmergencyStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EmergencyReportRepository extends JpaRepository<EmergencyReport, UUID> {

	List<EmergencyReport> findByStatus(EmergencyStatus status);
    
}
