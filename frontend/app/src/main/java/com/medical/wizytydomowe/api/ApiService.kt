package com.medical.wizytydomowe.api

import com.medical.wizytydomowe.api.login.LoginRequest
import com.medical.wizytydomowe.api.login.LoginResponse
import com.medical.wizytydomowe.api.registration.RegisterRequest
import com.medical.wizytydomowe.api.registration.RegisterResponse
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    // Rejestracja
    @POST("register/patient")
    fun register(@Body registerRequest: RegisterRequest): Call<ResponseBody>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("user/info")
    fun getUserInfo(@Header("Authorization") token: String): Call<UserInfoResponse>
}