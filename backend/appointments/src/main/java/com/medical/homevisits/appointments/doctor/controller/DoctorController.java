package com.medical.homevisits.appointments.doctor.controller;

import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.service.DoctorService;
import com.medical.homevisits.appointments.workplace.repository.WorkplaceRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DoctorController {

    private final DoctorService doctorService;
    private final WorkplaceRepository workplaceRepository;

    @Autowired
    public DoctorController(DoctorService doctorService, WorkplaceRepository workplaceRepository) {
        this.doctorService = doctorService;
        this.workplaceRepository = workplaceRepository;
    }

    @PostMapping("/api/doctors")
    public void createDoctor(@RequestBody createRequest request){
        Doctor doctor = new Doctor(request.getId(), request.getFirstName(), request.getLastName(), request.getSpecialization(), workplaceRepository.findById(request.getWorkPlaceId()).get());
        doctorService.create(doctor);
    }
}

@Getter
class   createRequest{
    private UUID id;
    private String firstName;
    private String lastName;
    private String specialization;
    private UUID workPlaceId;
}
