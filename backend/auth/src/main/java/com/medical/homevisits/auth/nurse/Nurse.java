package com.medical.homevisits.auth.nurse.entity;


import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@DiscriminatorValue("Nurse")
public class Nurse extends User {
    private String specialisation;
    private String academicDegree;
    private Doctor doctor;
    private String workPlace;

}
