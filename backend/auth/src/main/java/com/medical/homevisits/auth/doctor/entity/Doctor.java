package com.medical.homevisits.auth.doctor.entity;

import com.medical.homevisits.auth.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@DiscriminatorValue("Doctor")
public class Doctor extends User {
    private String specialisation;
    private String academicDegree;
    private String workPlace;

}
