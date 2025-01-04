package com.medical.homevisits.auth.paramedic.entity;

import com.medical.homevisits.auth.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@DiscriminatorValue("Paramedic")
public class Paramedic extends User {
    private String specialisation;
    private String academicDegree;
    private String workPlace;

}
