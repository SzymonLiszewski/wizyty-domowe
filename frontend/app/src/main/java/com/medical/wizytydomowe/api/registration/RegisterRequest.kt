package com.medical.wizytydomowe.api.registration

import java.time.LocalDate

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val dateOfBirth: String
)
