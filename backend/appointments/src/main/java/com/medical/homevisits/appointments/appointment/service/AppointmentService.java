package com.medical.homevisits.appointments.appointment.service;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import com.medical.homevisits.appointments.appointment.entity.AppointmentStatus;
import com.medical.homevisits.appointments.appointment.repository.AppointmentRepository;
import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.print.Doc;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository repository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentService(AppointmentRepository repository, DoctorRepository doctorRepository){
        this.repository = repository;
        this.doctorRepository = doctorRepository;
    }
    public void delete(UUID id){
        Appointment appointment = repository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment not fount"));
        repository.delete(appointment);
    }
    public void create(Appointment appointment){
        repository.save(appointment);
    }

    /**
     * Finding Appointments which fit given parameters
     * @param status - status RESERVED/AVAILABLE/CANCELED/COMPLETED
     * @param doctorId - doctors id
     * @param date - date of appointment (function will return appointments which start in given day)
     * @return - list of appointments which fit specification
     */
    public List<Appointment> getAppointments(AppointmentStatus status, UUID doctorId, LocalDate date){
        Specification<Appointment> spec = Specification.where(null);
        if (status != null){
            spec = spec.and((root, query, criteriaBuilder)-> criteriaBuilder.equal(root.get("status"), status));
        }
        if (doctorId!=null){
            if (doctorRepository.findById(doctorId).isPresent()){
                Doctor doctor = doctorRepository.findById(doctorId).get();
                spec = spec.and((root, query, criteriaBuilder)-> criteriaBuilder.equal(root.get("doctor"), doctor));
            }
            else{
                return new ArrayList<>();
            }
        }
        if (date != null){
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            spec = spec.and((root, query, criteriaBuilder)-> criteriaBuilder.between(root.get("appointmentStartTime"), startOfDay, endOfDay));
        }
        return repository.findAll(spec);
    }

    /**
     * function creates available appointments for given doctor in specified hours range for 1 month forward
     * @param doctorId - doctors id
     * @param dayOfWeek - appointments are created for every week on the same hours
     * @param appointmentsDuration - duration of appointment (each patient can only reserve whole time slot)
     */
    public void createAvailableAppoitnments(UUID doctorId,DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Duration appointmentsDuration){
        if (appointmentsDuration == null){
            appointmentsDuration=Duration.ofHours(1);
        }
        LocalDate today = LocalDate.now();
        LocalDate nextDate = today.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        LocalDate oneMonthLater = today.plusMonths(1);
        LocalTime currentTime = startTime;

        while (!nextDate.isAfter(oneMonthLater)) {
            while (!currentTime.isAfter(endTime)) {
                repository.save(Appointment.builder()
                        .appointmentStartTime(LocalDateTime.of(nextDate, currentTime))
                        .appointmentEndTime(LocalDateTime.of(nextDate, currentTime.plus(appointmentsDuration)))
                        .status(AppointmentStatus.AVAILABLE)
                        .doctor(doctorRepository.findById(doctorId).get())
                        .build());
                currentTime = currentTime.plus(appointmentsDuration);
            }
            currentTime = startTime;
            nextDate = nextDate.plusWeeks(1);
        }
    }
}
