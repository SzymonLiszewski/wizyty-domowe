package com.medical.wizytydomowe.api.userInfo

import com.medical.wizytydomowe.api.workplace.Workplace

data class UserInfoResponse(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val role: String?,
    val dateOfBirth: String?,
    val address: String?,
    val specialization: String?,
    val workPlace: Workplace?,
    val phoneNumber: String?
)