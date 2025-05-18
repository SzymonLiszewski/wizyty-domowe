package com.medical.homevisits.appointments.appointment.service;

import com.medical.homevisits.appointments.appointment.entity.Appointment;
import com.medical.homevisits.appointments.appointment.entity.AppointmentStatus;
import com.medical.homevisits.appointments.appointment.repository.AppointmentRepository;
import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;
import com.medical.homevisits.appointments.nurse.entity.Nurse;
import com.medical.homevisits.appointments.nurse.repository.NurseRepository;
import com.medical.homevisits.appointments.patient.entity.Patient;
import com.medical.homevisits.appointments.patient.repository.PatientRepository;
import com.medical.homevisits.appointments.workplace.entity.Workplace;
import com.medical.homevisits.appointments.workplace.repository.WorkplaceRepository;
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
    private final PatientRepository patientRepository;
    private final NurseRepository nurseRepository;
    private final WorkplaceRepository workplaceRepository;

    @Autowired
    public AppointmentService(AppointmentRepository repository, DoctorRepository doctorRepository, PatientRepository patientRepository, NurseRepository nurseRepository, WorkplaceRepository workplaceRepository){
        this.repository = repository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.nurseRepository = nurseRepository;
        this.workplaceRepository = workplaceRepository;
    }
    public void delete(UUID id){
        Appointment appointment = repository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment not found"));
        repository.delete(appointment);
    }
    public void create(Appointment appointment){
        repository.save(appointment);
    }

    public Appointment find(UUID id){
        return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment not found"));
    }
    /**
     * Finding Appointments which fit given parameters
     * @param status - status RESERVED/AVAILABLE/CANCELED/COMPLETED
     * @param doctorId - doctors id
     * @param date - date of appointment (function will return appointments which start in given day)
     * @param patientId - patients id
     * @return - list of appointments which fit specification
     */
    public List<Appointment> getAppointments(AppointmentStatus status, UUID doctorId, LocalDate date, UUID patientId, String city, UUID nurseId){
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
        if (patientId!=null){
            if (patientRepository.findById(patientId).isPresent()){
                Patient patient = patientRepository.findById(patientId).get();
                spec = spec.and((root, query, criteriaBuilder)-> criteriaBuilder.equal(root.get("patient"), patient));
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
        if (city != null){
            if (workplaceRepository.findByCity(city).isPresent()){
                Workplace workplace = workplaceRepository.findByCity(city).get();
                spec = spec.and((root, query, criteriaBuilder)-> criteriaBuilder.equal(root.get("doctor").get("workPlace"), workplace));
            }
            else{
                return new ArrayList<>();
            }
        }
        if (nurseId!=null){
            if (nurseRepository.findById(nurseId).isPresent()){
                Nurse nurse = nurseRepository.findById(nurseId).get();
                spec = spec.and((root, query, criteriaBuilder)-> criteriaBuilder.equal(root.get("nurse"), nurse));
            }
            else{
                return new ArrayList<>();
            }
        }
        return repository.findAll(spec);
    }

    /**
     * function creates available appointments for given doctor in specified hours range for 1 month forward
     * @param doctorId - doctors id
     * @param dayOfWeek - appointments are created for every week on the same hours
     * @param appointmentsDuration - duration of appointment (each patient can only reserve whole time slot)
     */
    public void createAvailableAppoitnments(UUID doctorId, UUID nurseId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Duration appointmentsDuration){
        //function do not allow to create appointments without doctor or nurse
        if (doctorId == null && nurseId == null){
            throw new IllegalArgumentException();
        }
        if (appointmentsDuration == null){
            appointmentsDuration=Duration.ofHours(1);
        }
        LocalDate today = LocalDate.now();
        LocalDate nextDate = today.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        LocalDate oneMonthLater = today.plusMonths(1);
        LocalTime currentTime = startTime;

        while (!nextDate.isAfter(oneMonthLater)) {
            while (!currentTime.isAfter(endTime)) {
                Doctor doctor = null;
                Nurse nurse = null;
                if (doctorId != null){
                    doctor = doctorRepository.findById(doctorId).get();
                }
                if (nurseId != null){
                    nurse = nurseRepository.findById(nurseId).get();
                }
                repository.save(Appointment.builder()
                        .appointmentStartTime(LocalDateTime.of(nextDate, currentTime))
                        .appointmentEndTime(LocalDateTime.of(nextDate, currentTime.plus(appointmentsDuration)))
                        .status(AppointmentStatus.AVAILABLE)
                        .doctor(doctor)
                        .nurse(nurse)
                        .build());
                currentTime = currentTime.plus(appointmentsDuration);
            }
            currentTime = startTime;
            nextDate = nextDate.plusWeeks(1);
        }
    }

    /**
     * function for registering patients on specific appointments - currently changes availability and sets patient's id TODO: maybe add notes or smth
     * @param appointmentId
     * @param patientId
     */
    public void registerPatient(UUID appointmentId, UUID patientId){
        Appointment appointment = this.find(appointmentId);
        Patient patient = patientRepository.findById(patientId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "patient not found"));
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.RESERVED);
        this.create(appointment); //this updates old appointment entity (without patient) with new (with patient)
    }
}
