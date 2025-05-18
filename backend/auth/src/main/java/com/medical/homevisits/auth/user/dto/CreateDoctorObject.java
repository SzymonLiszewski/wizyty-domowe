package com.medical.homevisits.auth.user.dto;

import com.medical.homevisits.auth.workplace.entity.Workplace;
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
    private UUID workPlaceId;
}
