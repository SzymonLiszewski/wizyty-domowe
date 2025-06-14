package com.medical.wizytydomowe.api.users

import java.io.Serializable

class Patient(
    id: String?,
    firstName: String?,
    lastName: String?,
    val email: String?,
    val phoneNumber: String?
) : Serializable, User(id, firstName, lastName)
