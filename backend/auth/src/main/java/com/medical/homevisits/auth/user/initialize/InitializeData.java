package com.medical.homevisits.auth.user.initialize;

import com.medical.homevisits.auth.patient.entity.Patient;
import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.nurse.entity.Nurse;
import com.medical.homevisits.auth.paramedic.entity.Paramedic;
import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.user.dto.CreateUserObject;
import com.medical.homevisits.auth.user.entity.User;
import com.medical.homevisits.auth.user.event.repository.UserEventRestRepository;
import com.medical.homevisits.auth.user.repository.UserRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.medical.homevisits.auth.user.service.CustomUserDetailsService;
import com.medical.homevisits.auth.user.service.UserService;
import com.medical.homevisits.auth.workplace.entity.Workplace;
import com.medical.homevisits.auth.workplace.repository.WorkplaceRepository;
import com.medical.homevisits.auth.workplace.service.WorkplaceService;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!build & !test") // Won't start if profile = build
public class InitializeData implements InitializingBean {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceService workplaceService;
    private final UserEventRestRepository userEventRestRepository;
    private final UserService userService;
    private Workplace workplace = Workplace.builder()
            .name("testWorkplace")
            .street("street 123")
            .city("Gdansk")
            .country("Poland")
            .build();

    @Autowired
    public InitializeData(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository, WorkplaceRepository workplaceRepository, WorkplaceService workplaceService, UserEventRestRepository userEventRestRepository, UserService userService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.workplaceRepository = workplaceRepository;
        this.workplaceService = workplaceService;
        this.userEventRestRepository = userEventRestRepository;
        this.userService = userService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (workplaceRepository.findByName(workplace.getName()).isEmpty()){
            workplaceService.create(workplace);
        }
        if (userRepository.findByEmail("admin@test.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("admin123"))
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user has been created!");
        }

        if (userRepository.findByEmail("patient@test.com").isEmpty()) {
            Patient patient = Patient.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("patient@test.com")
                    .password(passwordEncoder.encode("patient123"))
                    .phoneNumber("012345678")
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("06-05-2025"))
                    .build();
            userService.create(patient);
            System.out.println("Patient user has been created!");
        }

        if (userRepository.findByEmail("doctor@test.com").isEmpty()) {

            Doctor doctor = Doctor.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("doctor@test.com")
                    .password(passwordEncoder.encode("doctor123"))
                    .phoneNumber("012345678")
                    .specialization("General Practitioner")
                    .academicDegree("YES")
                    .workPlace(workplace)
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("06-05-2025"))
                    .build();
            userService.create(doctor);
            System.out.println("Doctor user has been created!");



        }
        if (userRepository.findByEmail("nurse@test.com").isEmpty()) {
            Nurse nurse = Nurse.builder()
                    .firstName("Alice")
                    .lastName("Brown")
                    .email("nurse@test.com")
                    .phoneNumber("012345678")
                    .password(passwordEncoder.encode("nurse123"))
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("06-05-2025"))
                    .specialization("General Practitioner")
                    .academicDegree("YES")
                    .workPlace(workplace)
                    .doctor("doctor@test.com")
                    .build();
            userService.create(nurse);
            System.out.println("Nurse user has been created!");
        }
        if (userRepository.findByEmail("paramedic@test.com").isEmpty()) {
            Paramedic paramedic = Paramedic.builder()
                    .firstName("Tom")
                    .lastName("White")
                    .email("paramedic@test.com")
                    .password(passwordEncoder.encode("paramedic123"))
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("06-05-2025"))
                    .phoneNumber("012345678")
                    .specialization("General Practitioner")
                    .academicDegree("YES")
                    .workPlace(workplace)
                    .build();
            userRepository.save(paramedic);
            System.out.println("Paramedic user has been created!");
        }
    }
}
