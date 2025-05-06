package com.medical.homevisits.auth.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
dto object used for communication between auth and appointments microservices
 **/
@Getter
@AllArgsConstructor
public class CreateUserObject{
    private UUID id;
    private String email;
    private String phoneNumber;
}
