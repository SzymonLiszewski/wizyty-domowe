package com.medical.homevisits.auth.user.controller;
import com.medical.homevisits.auth.patient.entity.Patient;
import com.medical.homevisits.auth.user.entity.User;
import com.medical.homevisits.auth.user.entity.User.UserInfoResponse;
import com.medical.homevisits.auth.user.repository.UserRepository;
import com.medical.homevisits.auth.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }
    @SuppressWarnings("deprecation")
	private String generateToken(String email, long duration) {
        User user = userRepository.findByEmail(email).get();
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getID());
        claims.put("role", user.getRole());
        return Jwts.builder()
                .setClaims(claims)
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
        	String email = Jwts.parserBuilder()
        		    .setSigningKey(jwtSecret)
        		    .build()
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
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("User with this email is already registered");
        }
        Patient patient = Patient.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userService.create(patient);
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * function for changing user data
     * @param request
     */
    @PostMapping("/userData")
    public Map<String, String> changeUserData(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ){
        String token = authorizationHeader.replace("Bearer ", "");

        String email = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        UUID userID = UUID.fromString(claims.get("id", String.class));

        if (!Objects.equals(email, request.getEmail())){
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient with given email already exists");
            }
            /**TODO: check if this is necessary.
            userService.delete(userRepository.findByEmail(email).get());
             **/
        }
        User user = userRepository.findById(userID).get();

        if (user instanceof Patient){
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            ((Patient) user).setAddress(request.getAddress());
            user.setDateOfBirth(request.getDateOfBirth());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setLastName(request.getLastName());
        }
        else{
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setDateOfBirth(request.getDateOfBirth());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setLastName(request.getLastName());
        }

        userService.create(user);

        token = generateToken(request.getEmail(), 3600000);
        String refreshToken = generateToken(request.getEmail(), 4*3600000);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("refresh_token", refreshToken);
        response.put("role", userRepository.findByEmail(request.getEmail()).get().getRole());
        return response;
    }

    @PostMapping("/user/password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).get();

        //setting up new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        //saving updates in repository
        userService.create(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/user/info")
    public ResponseEntity<UserInfoResponse> getCurrentUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");

        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if (email == null || userRepository.findByEmail(email).isEmpty()) {
                return ResponseEntity.status(401).body(null);
            }

            User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            UserInfoResponse userInfoResponse = currentUser.getUserInfo();

            return ResponseEntity.ok(userInfoResponse);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(null);
        }
    }

}

@Getter
@Setter
@NoArgsConstructor
class AuthRequest {
    private String email;
    private String password;

}

@Getter
@Setter
@NoArgsConstructor
class ChangePasswordRequest {
    private String email;
    private String password;
    private String newPassword;
}

@Getter
@Setter
@NoArgsConstructor
class RegisterRequest {
	@NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{9,15}", message = "Phone number must be between 9 and 15 digits")
    private String phoneNumber;

    @Temporal(TemporalType.DATE)
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;

    private String address;
}
@ControllerAdvice
 class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
