package com.medical.wizytydomowe.api.users

import java.io.Serializable

class Doctor(
    id: String?,
    firstName: String?,
    lastName: String?,
    val specialization: String?,
    val workPlace: String?
) : Serializable, User(id, firstName, lastName)
