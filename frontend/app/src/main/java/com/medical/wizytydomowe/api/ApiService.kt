package com.medical.wizytydomowe.api

import com.medical.wizytydomowe.api.login.LoginRequest
import com.medical.wizytydomowe.api.login.LoginResponse
import com.medical.wizytydomowe.api.registration.RegisterRequest
import com.medical.wizytydomowe.api.registration.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Rejestracja
    @POST("http://192.168.56.1:8081/api/auth/register/patient")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("http://192.168.56.1:8081/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}