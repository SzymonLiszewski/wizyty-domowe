package com.medical.homevisits.appointments.patient.entity;

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
@Table(name="patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ID;
}
