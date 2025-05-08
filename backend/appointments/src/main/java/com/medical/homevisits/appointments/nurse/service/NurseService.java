package com.medical.homevisits.appointments.nurse.service;

import com.medical.homevisits.appointments.nurse.entity.Nurse;
import com.medical.homevisits.appointments.nurse.repository.NurseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NurseService {
    private final NurseRepository nurseRepository;

    @Autowired
    public NurseService(NurseRepository nurseRepository) {
        this.nurseRepository = nurseRepository;
    }

    public void create(Nurse nurse){
        nurseRepository.save(nurse);
    }
}
