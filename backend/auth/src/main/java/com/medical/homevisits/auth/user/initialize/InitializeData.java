package com.medical.homevisits.auth.user.initialize;

import com.medical.homevisits.auth.user.entity.User;
import com.medical.homevisits.auth.user.repository.UserRepository;
import com.medical.homevisits.auth.user.service.CustomUserDetailsService;
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

    @Autowired
    public InitializeData(
           CustomUserDetailsService userDetailsService,
           PasswordEncoder passwordEncoder,
           UserRepository userRepository
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (userRepository.findByEmail("test").isEmpty()) {
            User admin = User.builder()
                    .ID(UUID.fromString("c4804e0f-769e-4ab9-9ebe-0578fb4f00a6"))
                    .email("test")
                    .password(passwordEncoder.encode("test"))
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user has been created!");
        }

    }
}

