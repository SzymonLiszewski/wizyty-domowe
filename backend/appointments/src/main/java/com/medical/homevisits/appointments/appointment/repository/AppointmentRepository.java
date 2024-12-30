package com.medical.homevisits.appointments.appointment.repository;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

}
