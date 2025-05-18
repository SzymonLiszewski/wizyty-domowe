package com.medical.homevisits.appointments.paramedic.entity;

import java.util.UUID;

import com.medical.homevisits.appointments.doctor.entity.Doctor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="paramedic")
public class Paramedic {
    @Id
    private UUID ID;
    private String firstName;
    private String lastName;
    private String specialization;
    private String workPlace;
}