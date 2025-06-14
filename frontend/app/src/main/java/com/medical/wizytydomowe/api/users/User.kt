package com.medical.wizytydomowe.api.users

import java.io.Serializable

open class User(
    val id: String?,
    val firstName: String?,
    val lastName: String?
): Serializable
