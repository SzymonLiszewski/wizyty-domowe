package com.medical.homevisits.auth.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 dto object used for communication between auth and appointments microservices
 **/
@Getter
@AllArgsConstructor
public class CreateDoctorObject{
    private UUID id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String workPlace;
}
