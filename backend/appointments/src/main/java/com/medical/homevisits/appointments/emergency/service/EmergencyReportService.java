package com.medical.homevisits.appointments.emergency.service;

import com.medical.homevisits.appointments.emergency.entity.EmergencyReport;
import com.medical.homevisits.appointments.emergency.entity.EmergencyStatus;
import com.medical.homevisits.appointments.emergency.repository.EmergencyReportRepository;

import com.medical.homevisits.appointments.patient.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class EmergencyReportService {

    private final EmergencyReportRepository repository;
    private final PatientRepository patientRepository;

    public EmergencyReportService(EmergencyReportRepository repository, PatientRepository patientRepository) {
        this.repository = repository;
        this.patientRepository = patientRepository;
    }

    public void create(EmergencyReport report) {
        repository.save(report);
    }

    public List<EmergencyReport> getReportsByPatient(UUID patientId) {
        return repository.findByPatient(patientRepository.findById(patientId).get());
    }

    public List<EmergencyReport> getAllReports() {
        return repository.findAll();
    }

    public EmergencyReport getReportById(UUID reportId) {
        return repository.findById(reportId).orElse(null);
    }

    public void update(EmergencyReport report) {
        if (repository.existsById(report.getId())) {
            repository.save(report);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Emergency report not found");
        }
    }


	public List<EmergencyReport> getReportsByStatus(EmergencyStatus status) {
	    return repository.findByStatus(status);
	}

	
}
