package com.medical.homevisits.appointments.doctor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="doctors")
public class Doctor {
    @Id
    private UUID ID;
    private String firstName;
    private String lastName;
    private String specialization;
}
