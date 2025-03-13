package com.medical.homevisits.auth.user.event.repository;

import com.medical.homevisits.auth.user.dto.CreateDoctorObject;
import com.medical.homevisits.auth.user.dto.CreateUserObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class UserEventRestRepository {
    private final RestTemplate restTemplate;

    @Autowired
    public UserEventRestRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createDoctor(CreateDoctorObject user){
        restTemplate.postForObject("/api/doctors", user, Void.class);
    }

    public void createPatient(CreateUserObject user){
        restTemplate.postForObject("/api/patients", user, Void.class);
    }

}
