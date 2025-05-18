package com.medical.homevisits.appointments.doctor.service;

import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;

import java.util.List;

import com.medical.homevisits.appointments.workplace.entity.Workplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }
    public List<Doctor> getDoctorsByWorkPlace(Workplace place) {
        return doctorRepository.findByWorkPlace(place);
    }

    
    public void create(Doctor doctor){
        doctorRepository.save(doctor);
    }
}
