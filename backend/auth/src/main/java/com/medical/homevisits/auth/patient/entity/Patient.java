package com.medical.homevisits.auth.patient.entity;

import com.medical.homevisits.auth.user.entity.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;




@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@DiscriminatorValue("Patient")
public class Patient extends User {
}
