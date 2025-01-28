package com.medical.wizytydomowe.api.userInfo

data class UserInfoResponse(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val role: String?,
    val dateOfBirth: String?,
    val address: String?,
    // Typo in backend so typo has to be in there as well =)
    val specialisation: String?,
    val academicDegree: String?,
    val workPlace: String?
)