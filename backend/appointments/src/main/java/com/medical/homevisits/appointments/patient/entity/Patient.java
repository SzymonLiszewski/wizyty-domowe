package com.medical.homevisits.appointments.patient.entity;

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
@Table(name="patients")
public class Patient {
    @Id
    private UUID ID;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
}
