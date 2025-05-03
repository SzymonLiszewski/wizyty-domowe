package com.medical.wizytydomowe.api.userInfo

data class UserInfoResponse(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val role: String?,
    val dateOfBirth: String?,
    val address: String?,
    val specialization: String?,
    val academicDegree: String?,
    val doctor: String?,
    val workPlace: String?
)