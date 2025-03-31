package com.medical.homevisits.appointments.prescription.service;

import com.medical.homevisits.appointments.prescription.entity.Prescription;
import com.medical.homevisits.appointments.prescription.repository.PrescriptionRepository;
import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.patient.entity.Patient;
import com.medical.homevisits.appointments.patient.repository.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, PatientRepository patientRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public Prescription createPrescription(UUID patientId, String medication, String dosage, String instructions) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient with given ID does not exist"));

        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setMedication(medication);
        prescription.setDosage(dosage);
        prescription.setNotes(instructions);

        return prescriptionRepository.save(prescription);
    }

    public List<Prescription> getPrescriptionsByPatient(UUID patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

	public List<Prescription> getPrescriptionsByDoctor(UUID doctorId) {
		 return prescriptionRepository.findByDoctorId(doctorId);
		 
	}

	public void createPrescription(Prescription prescription) {
		   prescriptionRepository.save(prescription);
		
	}

	public Optional<Prescription> findPrescriptionById(UUID prescriptionId) {
	    return prescriptionRepository.findById(prescriptionId);
	}

	public void updatePrescription(Prescription prescription) {
	    if (prescriptionRepository.existsById(prescription.getId())) {
	        prescriptionRepository.save(prescription);
	    } else {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found");
	    }
	}

}
