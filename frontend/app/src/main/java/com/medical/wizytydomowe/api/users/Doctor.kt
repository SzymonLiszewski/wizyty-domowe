package com.medical.wizytydomowe.api.users

import com.medical.wizytydomowe.api.workplace.Workplace
import java.io.Serializable

class Doctor(
    id: String?,
    firstName: String?,
    lastName: String?,
    val specialization: String?,
    val workPlace: Workplace?
) : Serializable, User(id, firstName, lastName)
