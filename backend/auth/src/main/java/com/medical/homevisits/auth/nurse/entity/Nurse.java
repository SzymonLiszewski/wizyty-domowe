package com.medical.homevisits.auth.nurse.entity;


import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.patient.entity.Patient;
import com.medical.homevisits.auth.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@DiscriminatorValue("Nurse")
public class Nurse extends User {
    private String specialization;
    private String academicDegree;
    private String doctor;
    private String workPlace;

}
