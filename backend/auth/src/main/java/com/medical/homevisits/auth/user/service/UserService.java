package com.medical.homevisits.auth.user.service;

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
        userEventRestRepository.createDoctor(new CreateUserObject(user.getID()));
    }
}
