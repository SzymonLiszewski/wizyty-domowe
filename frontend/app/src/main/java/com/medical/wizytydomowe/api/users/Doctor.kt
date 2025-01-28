package com.medical.wizytydomowe.api.users

import java.io.Serializable

data class Doctor(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val specialization: String?,
    val workPlace: String?
) : Serializable
