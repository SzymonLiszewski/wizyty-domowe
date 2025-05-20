package com.medical.wizytydomowe.api.editProfile

import java.io.Serializable

data class EditPasswordRequest(
    val email: String?,
    val password: String?,
    val newPassword: String?
): Serializable
