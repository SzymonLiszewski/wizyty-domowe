package com.medical.wizytydomowe.api.userInfo

import java.io.Serializable

data class EditUserInfoRequest(
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var phoneNumber: String?,
    var dateOfBirth: String?,
    var address: String?
) : Serializable
