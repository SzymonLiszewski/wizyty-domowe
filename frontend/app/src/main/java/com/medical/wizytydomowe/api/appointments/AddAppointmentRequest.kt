package com.medical.wizytydomowe.api.appointments

data class AddAppointmentRequest(
    val status: String?,
    val appointmentStartTime: String?,
    val appointmentEndTime: String?,
    val doctor: String?,
    val nurse: String?,
    val patient: String?,
    val address: String?,
    val notes: String?
)
