package com.medical.homevisits.auth.user.entity;

import com.medical.homevisits.auth.doctor.entity.Doctor;
import com.medical.homevisits.auth.nurse.entity.Nurse;
import com.medical.homevisits.auth.paramedic.entity.Paramedic;
import com.medical.homevisits.auth.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    public String getRole(){
        if (this instanceof Doctor){
            return "Doctor";
        }
        else if (this instanceof Patient){
            return "Patient";
        }
        else if (this instanceof Nurse){
            return "Nurse";
        }
        else if (this instanceof Paramedic){
            return "Paramedic";
        }
        return "OtherUser";
    }
    public  UserInfoResponse getUserInfo() {
    	
        UserInfoResponse response = new UserInfoResponse(
            this.ID,
            this.firstName,
            this.lastName,
            this.email,
            this.getRole(),
            this.dateOfBirth,
            null, 
            null, 
            null,
            null, 
            null  
        );

        if (this instanceof Doctor) {
            Doctor doctor = (Doctor) this;
            response.setSpecialization(doctor.getSpecialization());
            response.setAcademicDegree(doctor.getAcademicDegree());
            response.setWorkPlace(doctor.getWorkPlace());
        }

        if (this instanceof Patient) {
            Patient patient = (Patient) this;
            response.setAddress(patient.getAddress());
        }
        if (this instanceof Nurse) {
            Nurse nurse = (Nurse) this;
            response.setSpecialization(nurse.getSpecialization());
            response.setAcademicDegree(nurse.getAcademicDegree());
            response.setWorkPlace(nurse.getWorkPlace());
            response.setDoctor(nurse.getDoctor());
        }
        if (this instanceof Paramedic) {
        	Paramedic paramedic = (Paramedic) this;
            response.setSpecialization(paramedic.getSpecialization());
            response.setAcademicDegree(paramedic.getAcademicDegree());
            response.setWorkPlace(paramedic.getWorkPlace());
        }

        return response;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfoResponse {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        private Date dateOfBirth;

        private String address; 
        private String specialization; 
        private String academicDegree; 
        private String doctor; 
        private String workPlace;
    }


}
