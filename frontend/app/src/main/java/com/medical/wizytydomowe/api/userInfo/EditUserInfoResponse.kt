package com.medical.wizytydomowe.api.userInfo

import java.io.Serializable

data class EditUserInfoResponse(
    val id: String?,
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var role: String?,
    var dateOfBirth: String?,
    var address: String?,
    var specialization: String?,
    var academicDegree: String?,
    var doctor: String?,
    var workPlace: String?,
    var phoneNumber: String?
) : Serializable
