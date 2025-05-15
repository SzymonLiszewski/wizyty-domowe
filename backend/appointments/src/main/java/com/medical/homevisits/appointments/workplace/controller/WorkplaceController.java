package com.medical.homevisits.appointments.workplace.controller;

import com.medical.homevisits.appointments.workplace.entity.Workplace;
import com.medical.homevisits.appointments.workplace.service.WorkplaceService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;

@RestController
public class WorkplaceController {
    private final WorkplaceService workplaceService;

    @Autowired
    public WorkplaceController(WorkplaceService workplaceService) {
        this.workplaceService = workplaceService;
    }

    @PostMapping("/api/workplaces")
    public void createDoctor(@RequestBody createRequest request){
        Workplace workplace = new Workplace(request.getId(), request.getName(), request.getStreet(), request.getCity(), request.getPostalCode(), request.getCountry(), request.getPhoneNumber(), request.getEmail(), new ArrayList<>(), new ArrayList<>());
        workplaceService.create(workplace);
    }
}

@Getter
class createRequest{
    private UUID id;
    private String name;

    private String street;
    private String city;
    private String postalCode;
    private String country;

    private String phoneNumber;
    private String email;

}
