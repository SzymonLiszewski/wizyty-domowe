package com.medical.homevisits.auth.user.entity;

import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    public String getRole(){
        if (this instanceof Doctor){
            return "Doctor";
        }
        else if (this instanceof Patient){
            return "Patient";
        }
        return "OtherUser";
    }

}
