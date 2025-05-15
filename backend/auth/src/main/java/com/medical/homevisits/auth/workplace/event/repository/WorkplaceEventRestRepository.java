package com.medical.homevisits.auth.workplace.event.repository;

import com.medical.homevisits.auth.workplace.dto.CreateWorkplaceObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class WorkplaceEventRestRepository {
    private final RestTemplate restTemplate;

    @Autowired
    public WorkplaceEventRestRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createWorkplace(CreateWorkplaceObject user){
        restTemplate.postForObject("/api/workplaces", user, Void.class);
    }
}
