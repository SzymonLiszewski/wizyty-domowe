package com.medical.wizytydomowe.api.users

import java.io.Serializable

class Paramedic(
    id: String?,
    firstName: String?,
    lastName: String?,
    val workPlace: String?
): Serializable, User(id, firstName, lastName)
