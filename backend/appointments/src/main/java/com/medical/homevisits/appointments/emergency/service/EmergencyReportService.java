package com.medical.homevisits.appointments.emergency.service;

import com.medical.homevisits.appointments.emergency.entity.EmergencyReport;
import com.medical.homevisits.appointments.emergency.repository.EmergencyReportRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmergencyReportService {

    private final EmergencyReportRepository repository;

    public EmergencyReportService(EmergencyReportRepository repository) {
        this.repository = repository;
    }

    public void create(EmergencyReport report) {
        repository.save(report);
    }

    public Optional<EmergencyReport> getReportsByPatient(UUID patientId) {
        return repository.findById(patientId);
    }

    public List<EmergencyReport> getAllReports() {
        return repository.findAll();
    }

    public EmergencyReport getReportById(UUID reportId) {
        return repository.findById(reportId).orElse(null);
    }
}
