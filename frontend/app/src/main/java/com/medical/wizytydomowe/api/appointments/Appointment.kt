package com.medical.wizytydomowe.api.appointments

import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Patient
import java.io.Serializable

data class Appointment(
    val id: String?,
    val status: String?,
    val appointmentStartTime: String?,
    val appointmentEndTime: String?,
    val doctor: Doctor?,
    val patient: Patient?,
    val address: String?,
    val notes: String?
) : Serializable
