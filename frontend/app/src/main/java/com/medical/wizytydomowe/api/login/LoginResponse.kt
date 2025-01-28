package com.medical.wizytydomowe.api.login

data class LoginResponse(
    val refresh_token: String?,
    val token: String?,
    val role: String?
)
