package com.medical.homevisits.auth.nurse.entity;


import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.patient.entity.Patient;
import com.medical.homevisits.auth.user.entity.User;
import com.medical.homevisits.auth.workplace.entity.Workplace;
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

    @ManyToOne
    @JoinColumn(name="workPlace", nullable = true)
    private Workplace workPlace;

}
