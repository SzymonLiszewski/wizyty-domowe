package com.medical.homevisits.auth.user.repository;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.nurse.entity.Nurse;
import com.medical.homevisits.auth.paramedic.entity.Paramedic;
import com.medical.homevisits.auth.patient.entity.Patient;
import com.medical.homevisits.auth.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class UserRepositoryTests{
    @Autowired
    private UserRepository userRepository;

    @Test
    void userRepository_saveUser_findByEmail(){
        User user = User.builder()
                .email("user@email.com")
                .build();

        Doctor doctor = Doctor.builder()
                .email("doctor@email.com")
                .build();

        Patient patient = Patient.builder()
                .email("patient@email.com")
                .build();

        Nurse nurse = Nurse.builder()
                .email("nurse@email.com")
                .build();

        Paramedic paramedic = Paramedic.builder()
                .email("paramedic@email.com")
                .build();

        userRepository.save(user);
        userRepository.save(doctor);
        userRepository.save(patient);
        userRepository.save(nurse);
        userRepository.save(paramedic);

        Assertions.assertThat(userRepository.findByEmail("user@email.com")).isNotNull();
        Assertions.assertThat(userRepository.findByEmail("user@email.com").get().getEmail()).isEqualTo("user@email.com");
        Assertions.assertThat(userRepository.findByEmail("doctor@email.com")).isNotNull();
        Assertions.assertThat(userRepository.findByEmail("doctor@email.com").get().getEmail()).isEqualTo("doctor@email.com");
        Assertions.assertThat(userRepository.findByEmail("patient@email.com")).isNotNull();
        Assertions.assertThat(userRepository.findByEmail("patient@email.com").get().getEmail()).isEqualTo("patient@email.com");
        Assertions.assertThat(userRepository.findByEmail("nurse@email.com")).isNotNull();
        Assertions.assertThat(userRepository.findByEmail("nurse@email.com").get().getEmail()).isEqualTo("nurse@email.com");
        Assertions.assertThat(userRepository.findByEmail("paramedic@email.com")).isNotNull();
        Assertions.assertThat(userRepository.findByEmail("paramedic@email.com").get().getEmail()).isEqualTo("paramedic@email.com");
    }
}

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTests{

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        //RestTemplate restTemplate = restTemplateBuilder.build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    //transactional (in tests): after every test there is db rollback, so no need to worry about data stored in db
    @Transactional
    @Test
    public void registerPatientTest() throws Exception {
        String patientJson = """
            {
              "email": "test5@gmail.com",
              "firstName": "John",
              "lastName": "Doe",
              "password": "securePassword",
              "phoneNumber": "123456789",
              "dateOfBirth": "2000-01-01",
              "address": "qwe"
            }
        """;

        // Mocking the appointments service
        mockServer.expect(requestTo("http://localhost:8082/api/patients"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        mvc.perform(post("/api/auth/register/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    public void loginTest_correctCredentials_200() throws Exception {
        Patient patient = Patient.builder()
                .email("patient@email.com")
                .password(passwordEncoder.encode("patient123"))
                .build();

        userRepository.save(patient);

        String patientJson = """
            {
              "email": "patient@email.com",
              "password": "patient123"
            }
        """;

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    public void loginTest_wrongPassword_401() throws Exception {
        Patient patient = Patient.builder()
                .email("patient@email.com")
                .password(passwordEncoder.encode("patient123"))
                .build();

        userRepository.save(patient);

        String patientJson = """
            {
              "email": "patient@email.com",
              "password": "incorrectPassword"
            }
        """;

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().is4xxClientError());
    }

    @Transactional
    @Test
    public void changePassword_correct_200() throws Exception {
        Patient patient = Patient.builder()
                .email("patient@email.com")
                .password(passwordEncoder.encode("patient123"))
                .build();

        userRepository.save(patient);

        String patientJson = """
            {
              "email": "patient@email.com",
              "password": "patient123",
              "newPassword": "patient1234"
            }
        """;

        // Mocking the appointments service
        mockServer.expect(requestTo("http://localhost:8082/api/patients"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        mvc.perform(post("/api/auth/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk());

        patientJson = """
            {
              "email": "patient@email.com",
              "password": "patient1234"
            }
        """;

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    public void changeUserData_correctToken_200() throws Exception {
        Patient patient = Patient.builder()
                .email("patient@email.com")
                .password(passwordEncoder.encode("patient123"))
                .build();

        userRepository.save(patient);

        String patientJson = """
            {
              "email": "patient@email.com",
              "password": "patient123"
            }
        """;

        MvcResult result =  mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText();
        System.out.println(token);

        patientJson = """
            {
                "email": "changed@gmail.com",
                "firstName": "changedFirstName",
                "lastName": "changedLastName",
                "password": "securePassword",
                "phoneNumber": "123456789",
                "dateOfBirth": "2000-01-01",
                "address": "changed"
            }
        """;

        // Mocking the appointments service
        mockServer.expect(requestTo("http://localhost:8082/api/patients"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        mvc.perform(post("/api/auth/userData")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                        .andExpect(status().isOk());

        User patient1 = userRepository.findByEmail("changed@gmail.com").get();
        Assertions.assertThat(patient1.getFirstName()).isEqualTo("changedFirstName");
        Assertions.assertThat(patient1.getLastName()).isEqualTo("changedLastName");

        Optional<User> patientOld = userRepository.findByEmail("patient@email.com");
        Assertions.assertThat(patientOld).isEmpty();
    }

    @Transactional
    @Test
    public void changePassword_incorrectPassword_401() throws Exception {
        Patient patient = Patient.builder()
                .email("patient@email.com")
                .password(passwordEncoder.encode("patient123"))
                .build();

        userRepository.save(patient);

        String patientJson = """
            {
              "email": "patient@email.com",
              "password": "incorrect",
              "newPassword": "patient1234"
            }
        """;

        // Mocking the appointments service
        mockServer.expect(requestTo("http://localhost:8082/api/patients"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        mvc.perform(post("/api/auth/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().is4xxClientError());
    }


}
