package com.medical.homevisits.appointments.nurse.controller;

import com.medical.homevisits.appointments.nurse.entity.Nurse;
import com.medical.homevisits.appointments.nurse.service.NurseService;
import com.medical.homevisits.appointments.workplace.repository.WorkplaceRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class NurseController {
    private final NurseService nurseService;
    private final WorkplaceRepository workplaceRepository;

    @Autowired
    public NurseController(NurseService nurseService, WorkplaceRepository workplaceRepository) {
        this.nurseService = nurseService;
        this.workplaceRepository = workplaceRepository;
    }

    @PostMapping("/api/nurses")
    public void createNurse(@RequestBody createRequest request){
        Nurse nurse = new Nurse(request.getId(), request.getFirstName(), request.getLastName(), workplaceRepository.findById(request.getWorkPlaceId()).get());
        nurseService.create(nurse);
    }
}

@Getter
class   createRequest{
    private UUID id;
    private String firstName;
    private String lastName;
    private UUID workPlaceId;
}
