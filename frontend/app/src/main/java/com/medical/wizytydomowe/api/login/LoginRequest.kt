package com.medical.wizytydomowe.api.login

data class LoginRequest(
    val email: String,
    val password: String
)