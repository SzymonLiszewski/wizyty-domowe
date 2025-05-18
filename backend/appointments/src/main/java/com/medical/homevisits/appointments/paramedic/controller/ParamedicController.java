package com.medical.homevisits.appointments.paramedic.controller;

import com.medical.homevisits.appointments.paramedic.entity.Paramedic;
import com.medical.homevisits.appointments.paramedic.service.ParamedicService;
import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;
import com.medical.homevisits.appointments.doctor.service.DoctorService;
import com.medical.homevisits.appointments.workplace.repository.WorkplaceRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ParamedicController {

    private final ParamedicService paramedicService;
    private final WorkplaceRepository workplaceRepository;
    
    @Autowired
    public ParamedicController(ParamedicService paramedicService, WorkplaceRepository workplaceRepository) {
        this.paramedicService = paramedicService;
        this.workplaceRepository = workplaceRepository;
    }

    @PostMapping("/api/paramedics")
    public void createDoctor(@RequestBody createRequest request){
    	Paramedic paramedic = new Paramedic(request.getId(), request.getFirstName(), request.getLastName(), request.getSpecialization(), workplaceRepository.findById(request.getWorkPlaceId()).get());
    	paramedicService.create(paramedic);
    }
    @GetMapping("/api/paramedics/workplace/{place}")
    public List<Paramedic> getDoctorsByWorkPlace(@PathVariable UUID place) {
        return paramedicService.getParamedicByWorkPlace(workplaceRepository.findById(place).get());
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
