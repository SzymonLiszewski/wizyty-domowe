package com.medical.homevisits.appointments.doctor.entity;

import com.medical.homevisits.appointments.workplace.entity.Workplace;
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
@Table(name="doctors")
public class Doctor {
    @Id
    private UUID ID;
    private String firstName;
    private String lastName;
    private String specialization;

    @ManyToOne
    @JoinColumn(name="workPlace", nullable = true)
    private Workplace workPlace;
}
