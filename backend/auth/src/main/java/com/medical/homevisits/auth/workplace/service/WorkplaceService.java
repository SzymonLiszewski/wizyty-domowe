package com.medical.homevisits.auth.workplace.service;

import com.medical.homevisits.auth.workplace.dto.CreateWorkplaceObject;
import com.medical.homevisits.auth.workplace.entity.Workplace;
import com.medical.homevisits.auth.workplace.event.repository.WorkplaceEventRestRepository;
import com.medical.homevisits.auth.workplace.repository.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkplaceService {
    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceEventRestRepository workplaceEventRestRepository;

    @Autowired
    public WorkplaceService(WorkplaceRepository workplaceRepository, WorkplaceEventRestRepository workplaceEventRestRepository) {
        this.workplaceRepository = workplaceRepository;
        this.workplaceEventRestRepository = workplaceEventRestRepository;
    }

    public void create(Workplace workplace){
        workplaceRepository.save(workplace);
        workplaceEventRestRepository.createWorkplace(new CreateWorkplaceObject(workplace.getID(), workplace.getName(), workplace.getStreet(), workplace.getCity(), workplace.getPostalCode(), workplace.getCountry(), workplace.getPhoneNumber(), workplace.getEmail()));
    }
}
