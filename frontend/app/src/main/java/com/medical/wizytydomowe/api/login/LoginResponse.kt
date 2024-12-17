package com.medical.wizytydomowe.api.login

data class LoginResponse(
    val token: String?,
    val role: String?
)
