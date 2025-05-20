package com.medical.wizytydomowe.api.emergency

import com.medical.wizytydomowe.api.users.Paramedic
import com.medical.wizytydomowe.api.users.Patient
import java.io.Serializable

data class Emergency(
    val id: String?,
    val patient: Patient?,
    val paramedic: Paramedic?,
    val status: String?,
    val emergencyReportTime: String?,
    val address: String?,
    val description: String?
) : Serializable
