package com.medical.homevisits.auth.workplace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateWorkplaceObject {
    private UUID ID;
    private String name;

    private String street;
    private String city;
    private String postalCode;
    private String country;

    private String phoneNumber;
    private String email;
}
