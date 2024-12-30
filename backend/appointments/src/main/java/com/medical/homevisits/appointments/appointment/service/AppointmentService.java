package com.medical.homevisits.appointments.appointment.service;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import com.medical.homevisits.appointments.appointment.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository repository;

    @Autowired
    public AppointmentService(AppointmentRepository repository){
        this.repository = repository;
    }
    public void delete(UUID id){
        Appointment appointment = repository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment not fount"));
        repository.delete(appointment);
    }
    public void create(Appointment appointment){
        repository.save(appointment);
    }
}
