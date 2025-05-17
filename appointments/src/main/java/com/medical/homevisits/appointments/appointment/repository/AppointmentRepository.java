package com.medical.homevisits.appointments.appointment.repository;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import com.medical.homevisits.appointments.appointment.entity.AppointmentStatus;
import com.medical.homevisits.appointments.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByAppointmentStartTime(LocalDateTime appointmentStartDate);
    List<Appointment> findByStatusAndDoctorAndAppointmentStartTime(AppointmentStatus status, Doctor doctor, LocalDateTime appointmentStartDate);
    List<Appointment> findByStatusAndDoctor(AppointmentStatus status, Doctor doctor);
    List<Appointment> findByStatusAndAppointmentStartTime(AppointmentStatus status, LocalDateTime appointmentStartDate);
    List<Appointment> findByDoctorAndAppointmentStartTime(Doctor doctor, LocalDateTime appointmentStartDate);
}
