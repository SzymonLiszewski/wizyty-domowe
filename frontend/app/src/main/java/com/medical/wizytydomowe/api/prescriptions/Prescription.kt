package com.medical.wizytydomowe.api.prescriptions

import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Patient
import java.io.Serializable

data class Prescription(
    val id: String?,
    val doctor: Doctor?,
    val patient: Patient?,
    val date: String?,
    val medication: String?,
    val dosage: String?,
    val notes: String?
) : Serializable
