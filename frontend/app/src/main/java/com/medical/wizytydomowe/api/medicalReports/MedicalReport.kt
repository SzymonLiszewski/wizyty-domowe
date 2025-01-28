package com.medical.wizytydomowe.api.medicalReports

import java.io.Serializable

data class MedicalReport(
    val id: String?,
    val status: String?,
    val firstName: String?,
    val lastName: String?,
    val date: String?,
    val address: String?,
    val description: String?
) : Serializable
