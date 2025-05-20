package com.medical.wizytydomowe.api.prescriptions


data class PrescriptionRequest(
    val patient: String?,
    val medication: String?,
    val dosage: String?,
    val notes: String?,
    val date: String?
)
