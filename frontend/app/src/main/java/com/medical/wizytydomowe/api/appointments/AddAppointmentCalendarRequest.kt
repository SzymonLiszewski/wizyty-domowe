package com.medical.wizytydomowe.api.appointments

import java.io.Serializable

data class AddAppointmentCalendarRequest(
    val dayOfWeek: String?,
    val startTime: String?,
    val endTime: String?,
    val appointmentsDuration: String?
): Serializable
