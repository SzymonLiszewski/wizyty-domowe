package com.medical.wizytydomowe.api.users

import com.medical.wizytydomowe.api.workplace.Workplace
import java.io.Serializable

class Nurse(
    id: String?,
    firstName: String?,
    lastName: String?,
    val workPlace: Workplace?
): Serializable, User(id, firstName, lastName)
