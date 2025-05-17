package com.medical.homevisits.appointments.nurse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name="nurses")
public class Nurse {
    @Id
    private UUID ID;
    private String firstName;
    private String lastName;
    private String workPlace;
}
