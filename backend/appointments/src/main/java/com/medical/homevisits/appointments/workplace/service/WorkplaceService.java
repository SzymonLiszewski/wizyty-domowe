package com.medical.homevisits.appointments.workplace.service;

import com.medical.homevisits.appointments.workplace.entity.Workplace;
import com.medical.homevisits.appointments.workplace.repository.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkplaceService {
    private final WorkplaceRepository workplaceRepository;

    @Autowired
    public WorkplaceService(WorkplaceRepository workplaceRepository) {
        this.workplaceRepository = workplaceRepository;
    }

    public void create(Workplace workplace){
        workplaceRepository.save(workplace);
    }
}
