package com.medical.homevisits.auth.user.controller;
import com.medical.homevisits.auth.patient.entity.Patient;
import com.medical.homevisits.auth.user.entity.User;
import com.medical.homevisits.auth.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    private String generateToken(String email, long duration) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + duration))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = generateToken(request.getEmail(), 3600000);
        String refreshToken = generateToken(request.getEmail(), 4*3600000);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("refresh_token", refreshToken);
        response.put("role", userRepository.findByEmail(request.getEmail()).get().getRole());
        return response;
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");

        try {
            String email = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();

            if (email == null || userRepository.findByEmail(email).isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
            }

            String newJwtToken = generateToken(email, 3600000); 
            return ResponseEntity.ok(Map.of("jwt_token", newJwtToken));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
        }
    }

    @PostMapping("/register/patient")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("User with this email is already registered");
        }
        Patient patient = Patient.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(patient);
        String jwtToken = generateToken(request.getEmail(), 3600000); 
        String refreshToken = generateToken(request.getEmail(), 3600000*4); 
        return ResponseEntity.ok("User registered successfully");
    }
}

@Getter
@Setter
class AuthRequest {
    private String email;
    private String password;

}

@Getter
@Setter
class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    public Map<String, String> validate() {
        Map<String, String> errors = new HashMap<>();

        if (firstName == null || firstName.trim().isEmpty()) {
            errors.put("firstName", "First name is required");
        } else if (firstName.length() < 2 || firstName.length() > 50) {
            errors.put("firstName", "First name must be between 2 and 50 characters");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            errors.put("lastName", "Last name is required");
        } else if (lastName.length() < 2 || lastName.length() > 50) {
            errors.put("lastName", "Last name must be between 2 and 50 characters");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.put("email", "Invalid email format");
        }

        if (password == null || password.length() < 8) {
            errors.put("password", "Password must be at least 8 characters long");
        }

        if (phoneNumber == null || !phoneNumber.matches("\\d{9,15}")) {
            errors.put("phoneNumber", "Phone number must be between 9 and 15 digits");
        }

        if (dateOfBirth == null || dateOfBirth.after(new Date())) {
            errors.put("dateOfBirth", "Date of birth must be in the past");
        }

        return errors;
    }
}
