package com.medical.homevisits.auth.user.service;

import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.nurse.entity.Nurse;
import com.medical.homevisits.auth.paramedic.entity.Paramedic;
import com.medical.homevisits.auth.patient.entity.Patient;
import com.medical.homevisits.auth.user.dto.CreateDoctorObject;
import com.medical.homevisits.auth.user.dto.CreateNurseObject;
import com.medical.homevisits.auth.user.dto.CreateParamedicObject;
import com.medical.homevisits.auth.user.dto.CreateUserObject;
import com.medical.homevisits.auth.user.entity.User;
import com.medical.homevisits.auth.user.event.repository.UserEventRestRepository;
import com.medical.homevisits.auth.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserEventRestRepository userEventRestRepository;

    public UserService(UserRepository userRepository, UserEventRestRepository userEventRestRepository) {
        this.userRepository = userRepository;
        this.userEventRestRepository = userEventRestRepository;
    }

    public void create(User user){
        userRepository.save(user);
        if (user instanceof Doctor){
            userEventRestRepository.createDoctor(new CreateDoctorObject(user.getID(), user.getFirstName(), user.getLastName(), ((Doctor) user).getSpecialization(), ((Doctor) user).getWorkPlace().getID()));
        } else if (user instanceof Patient) {
            userEventRestRepository.createPatient(new CreateUserObject(user.getID(), user.getEmail(), user.getPhoneNumber(), user.getFirstName(), user.getLastName()));
        }
        else if (user instanceof Nurse) {
            userEventRestRepository.createNurse(new CreateNurseObject(user.getID(), user.getFirstName(), user.getLastName(), ((Nurse) user).getWorkPlace().getID()));
        }
        else if (user instanceof Paramedic) {
            userEventRestRepository.createParamedic(new CreateParamedicObject(user.getID(), user.getFirstName(), user.getLastName(),((Paramedic) user).getSpecialization(), ((Paramedic) user).getWorkPlace().getID()));
        }
        //TODO: extend for other classes, use in initialize file
    }

    public void delete(User user){
        userRepository.delete(user);
        //TODO: eventrestrepository delete, (currently if user change email)
    }
}
