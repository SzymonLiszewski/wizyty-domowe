package com.medical.homevisits.auth.doctor.entity;

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
@DiscriminatorValue("Doctor")
public class Doctor extends User {
    private String specialization;
    private String academicDegree;

    @ManyToOne
    @JoinColumn(name="workPlace", nullable = true)
    private Workplace workPlace;

}
