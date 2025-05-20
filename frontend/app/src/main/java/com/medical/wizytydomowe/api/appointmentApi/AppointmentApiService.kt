package com.medical.wizytydomowe.api.appointmentApi

import com.medical.wizytydomowe.api.appointments.AddAppointmentRequest
import com.medical.wizytydomowe.api.emergency.ChangeStatusRequest
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.prescriptions.Prescription
import retrofit2.Call
import com.medical.wizytydomowe.api.prescriptions.PrescriptionRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface AppointmentApiService {

    @POST("api/prescriptions")
    fun addNewPrescription(@Header("Authorization") token: String, @Body prescriptionRequest: PrescriptionRequest): Call<Unit>

    @GET("api/prescriptions/doctors")
    fun getDoctorPrescriptions(@Header("Authorization") token: String): Call<List<Prescription>>

    @GET("api/prescriptions/patients")
    fun getPatientPrescriptions(@Header("Authorization") token: String): Call<List<Prescription>>

    @POST("api/emergency")
    fun addNewEmergency(@Header("Authorization") token: String, @Body emergency: Emergency): Call<ResponseBody>

    @GET("api/emergency/available")
    fun getAvailableEmergency() : Call<List<Emergency>>

    @PUT("api/emergency/{reportId}/assign")
    fun assignEmergency(@Header("Authorization") token: String, @Path("reportId") reportId: String): Call<ResponseBody>

    @GET("api/emergency/patients")
    fun getPatientEmergency(@Header("Authorization") token: String): Call<List<Emergency>>

    @GET("api/emergency/paramedics")
    fun getParamedicEmergency(@Header("Authorization") token: String): Call<List<Emergency>>

    @PUT("api/emergency/{reportId}/status")
    fun finishEmergency(@Header("Authorization") token: String, @Path("reportId") reportId: String, @Body changeStatusRequest: ChangeStatusRequest): Call<ResponseBody>

    @POST("api/appointments")
    fun addSingleAppointment(@Body addAppointmentRequest: AddAppointmentRequest): Call<Unit>
}