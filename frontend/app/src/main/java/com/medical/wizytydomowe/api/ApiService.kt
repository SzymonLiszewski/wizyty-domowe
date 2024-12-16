package com.medical.wizytydomowe.api

import com.medical.wizytydomowe.api.registration.RegisterRequest
import com.medical.wizytydomowe.api.registration.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Rejestracja
    @POST("/api/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>
}