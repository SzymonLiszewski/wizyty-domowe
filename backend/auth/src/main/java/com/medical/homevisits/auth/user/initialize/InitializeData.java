package com.medical.homevisits.auth.user.initialize;

import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.user.dto.CreateUserObject;
import com.medical.homevisits.auth.user.entity.User;
import com.medical.homevisits.auth.user.event.repository.UserEventRestRepository;
import com.medical.homevisits.auth.user.repository.UserRepository;
import com.medical.homevisits.auth.user.service.CustomUserDetailsService;
import com.medical.homevisits.auth.user.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InitializeData implements InitializingBean {


    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserEventRestRepository userEventRestRepository;
    private final UserService userService;

    @Autowired
    public InitializeData(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository, UserEventRestRepository userEventRestRepository, UserService userService
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userEventRestRepository = userEventRestRepository;
        this.userService = userService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (userRepository.findByEmail("test").isEmpty()) {
            User admin = User.builder()
                    .email("test")
                    .password(passwordEncoder.encode("test"))
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user has been created!");
        }
        if (userRepository.findByEmail("test2").isEmpty()) {
            Doctor doctor = Doctor.builder()
                    .email("test2")
                    .password(passwordEncoder.encode("test"))
                    .build();
            userService.create(doctor);
            System.out.println("Doctor has been created with id "+doctor.getID().toString());
        }
    }
}

