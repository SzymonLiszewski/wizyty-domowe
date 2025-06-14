package com.medical.wizytydomowe.api.editProfile

data class EditUserInfoResponse(
    val refresh_token: String?,
    val token: String?,
    val role: String?
)
