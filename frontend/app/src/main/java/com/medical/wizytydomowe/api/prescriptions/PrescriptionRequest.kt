package com.medical.wizytydomowe.api.prescriptions


data class PrescriptionRequest(
    val id: String?,
    val patient: String?,
    val medication: String?,
    val dosage: String?,
    val notes: String?
)
