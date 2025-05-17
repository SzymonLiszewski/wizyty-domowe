package com.medical.wizytydomowe.api.userInfo

data class EditUserInfoResponse(
    val refresh_token: String?,
    val token: String?,
    val role: String?
)
