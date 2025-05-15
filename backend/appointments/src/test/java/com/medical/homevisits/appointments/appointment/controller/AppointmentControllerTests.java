package com.medical.homevisits.appointments.appointment.controller;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import com.medical.homevisits.appointments.appointment.repository.AppointmentRepository;
import com.medical.homevisits.appointments.appointment.service.AppointmentService;
import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.service.DoctorService;
import com.medical.homevisits.appointments.patient.entity.Patient;
import com.medical.homevisits.appointments.nurse.entity.Nurse;
import com.medical.homevisits.appointments.patient.repository.PatientRepository;
import com.medical.homevisits.appointments.patient.service.PatientService;
import com.medical.homevisits.appointments.workplace.entity.Workplace;
import com.medical.homevisits.appointments.workplace.service.WorkplaceService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AppointmentControllerTests {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    MockMvc mvc;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DoctorService doctorService;

    @Autowired
    PatientService patientService;

    @Autowired
    WorkplaceService workplaceService;

    Workplace workplace = Workplace.builder()
            .ID(UUID.fromString("806654e3-daa5-47da-a675-37382b707ea1"))
            .build();

    @BeforeEach
    void setup() {
        //create test workplace
        workplaceService.create(workplace);

        //create test users
        Doctor testDoctor = Doctor.builder()
                .ID(UUID.fromString("9b2a1d84-4f74-41c8-9bc5-7d73c0a1cb69"))
                .firstName("test")
                .firstName("test")
                .specialization("test")
                .workPlace(workplace)
                .build();
        Doctor testDoctor2 = Doctor.builder()
                .ID(UUID.fromString("2d8cc08f-d1f9-4d76-8ac6-8abde976dc9a"))
                .firstName("test2")
                .firstName("test2")
                .specialization("test2")
                .workPlace(workplace)
                .build();
        Nurse testNurse = Nurse.builder()
                .ID(UUID.fromString("64c7448c-0f65-4ceb-aa94-e09590dccaf3"))
                .firstName("test2")
                .firstName("test2")
                .workPlace(workplace)
                .build();
        Patient testPatient = Patient.builder().ID(UUID.fromString("875e5515-d6a0-45f0-b1a0-00e624186164")).build();

        doctorService.create(testDoctor);
        doctorService.create(testDoctor2);
        patientService.create(testPatient);
        //create test appointments
        //todo: add nurse to test appointments
        appointmentService.createAvailableAppoitnments(testDoctor.getID(), null ,DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), Duration.ofHours(1));
        appointmentService.createAvailableAppoitnments(testDoctor2.getID(), null,DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), Duration.ofHours(1));


    }

    @Transactional
    @Test
    public void getAvailableAppointments_PatientWithNoParameters_returnAllAppointments() throws Exception {

        mvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("AVAILABLE")))
                .andExpect(jsonPath("$[1].status", is("AVAILABLE")))
                .andExpect(jsonPath("$[0].doctor.firstName", is("test")));
    }

    @Transactional
    @Test
    public void getAvailableAppointments_PatientWithDoctorParameter_returnAppointmentsForDoctor2() throws Exception {

        mvc.perform(get("/api/appointments?doctorId=2d8cc08f-d1f9-4d76-8ac6-8abde976dc9a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("AVAILABLE")))
                .andExpect(jsonPath("$[0].doctor.firstName", is("test2")));
    }

    @Transactional
    @Test
    public void getDoctorAppointments_correctDoctorToken_returnAppointmentsForDoctor() throws Exception {

        String doctorToken = mockDoctorJWT();

        mvc.perform(get("/api/appointments/doctors")
                .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("AVAILABLE")))
                .andExpect(jsonPath("$[0].doctor.firstName", is("test")));
    }
    @Transactional
    @Test
    public void getDoctorAppointments_noAuthToken_return401() throws Exception {

        mvc.perform(get("/api/appointments/doctors"))
                .andExpect(status().is4xxClientError());
    }

    @Transactional
    @Test
    public void getPatientAppointments_correctPatientToken_returnAppointmentsForDoctor() throws Exception {

        String patientToken = mockPatientJWT();

        Patient testPatient2 = Patient.builder().ID(UUID.fromString("c384e706-bf44-46a2-beff-3f1663fd7cb1")).build();
        patientService.create(testPatient2);

        patientRepository.flush();

        //registering patient for appointment
        appointmentService.create(Appointment.builder()
                        .notes("test")
                        .patient(testPatient2)
                .build());
        //appointmentService.registerPatient(UUID.fromString("a4ecbdf6-af03-485b-b72b-10d88a2110e3"), UUID.fromString("875e5515-d6a0-45f0-b1a0-00e624186164"));

        mvc.perform(get("/api/appointments/patients")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notes", is("test")));
    }

    @Transactional
    @Test
    public void getPatientAppointments_noAuthToken_returnAppointmentsForDoctor() throws Exception {

        mvc.perform(get("/api/appointments/patients"))
                .andExpect(status().is4xxClientError());
    }

    private String mockDoctorJWT() {
        Doctor testDoctor = Doctor.builder()
                .ID(UUID.fromString("9b2a1d84-4f74-41c8-9bc5-7d73c0a1cb69"))
                .firstName("test")
                .firstName("test")
                .specialization("test")
                .workPlace(workplace)
                .build();
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", testDoctor.getID());
        claims.put("role", "Doctor");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    private String mockPatientJWT() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "c384e706-bf44-46a2-beff-3f1663fd7cb1");
        claims.put("role", "Patient");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
}

