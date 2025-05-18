package com.medical.homevisits.auth.paramedic.entity;

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
@DiscriminatorValue("Paramedic")
public class Paramedic extends User {
    private String specialization;
    private String academicDegree;
    private String workPlace;

}
