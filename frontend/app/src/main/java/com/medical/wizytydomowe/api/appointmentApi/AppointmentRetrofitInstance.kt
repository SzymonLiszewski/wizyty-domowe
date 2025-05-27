package com.medical.wizytydomowe.api.appointmentApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppointmentRetrofitInstance {

    const val BASE_URL = "http://192.168.76.214:8082/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val appointmentApiService: AppointmentApiService = retrofit.create(AppointmentApiService::class.java)

}