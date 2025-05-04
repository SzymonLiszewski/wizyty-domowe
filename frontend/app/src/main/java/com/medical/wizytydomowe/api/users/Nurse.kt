package com.medical.wizytydomowe.api.users

import java.io.Serializable

data class Nurse(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val workPlace: String?
): Serializable
