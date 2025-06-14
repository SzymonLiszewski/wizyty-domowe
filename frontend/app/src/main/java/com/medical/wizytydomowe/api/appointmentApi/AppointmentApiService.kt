package com.medical.wizytydomowe.api.appointmentApi

import com.medical.wizytydomowe.api.appointments.AddAppointmentCalendarRequest
import com.medical.wizytydomowe.api.appointments.AddAppointmentRequest
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.appointments.AppointmentRegisterRequest
import com.medical.wizytydomowe.api.emergency.EmergencyChangeStatusRequest
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.prescriptions.Prescription
import retrofit2.Call
import com.medical.wizytydomowe.api.prescriptions.PrescriptionRequest
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Nurse
import com.medical.wizytydomowe.api.users.Patient
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


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
    fun finishEmergency(@Header("Authorization") token: String, @Path("reportId") reportId: String, @Body emergencyChangeStatusRequest: EmergencyChangeStatusRequest): Call<ResponseBody>

    @POST("api/appointments")
    fun addSingleAppointment(@Body addAppointmentRequest: AddAppointmentRequest, @Header("Authorization") token: String): Call<Unit>

    @POST("api/appointments/doctors")
    fun addMonthAppointment(@Header("Authorization") token: String, @Body addAppointmentCalendarRequest: AddAppointmentCalendarRequest): Call<Unit>

    @GET("api/appointments/doctors")
    fun getDoctorAppointment(@Header("Authorization") token: String, @Query("appointmentDate") appointmentDate: String?): Call<List<Appointment>>

    @GET("api/appointments/patients")
    fun getPatientAppointment(@Header("Authorization") token: String): Call<List<Appointment>>

    @GET("api/appointments/nurses")
    fun getNurseAppointment(@Header("Authorization") token: String, @Query("appointmentDate") appointmentDate: String?): Call<List<Appointment>>

    @POST("api/appointments/register")
    fun registerPatientOnAppointment(@Header("Authorization") token: String, @Body appointmentRegisterRequest: AppointmentRegisterRequest): Call<Unit>

    @PUT("api/appointments/{id}/cancel")
    fun cancelAppointment(@Path("id") id: String, @Header("Authorization") token: String): Call<Unit>

    @PUT("api/appointments/{id}/complete")
    fun finishAppointment(@Path("id") id: String, @Header("Authorization") token: String): Call<ResponseBody>

    @GET("api/appointments/nurses/available")
    fun getNursesAvailable(@Query("preferredCity") preferredCity: String?, @Query("preferredLastName") preferredLastName: String?): Call<List<Nurse>>

    @GET("api/appointments/doctors/available")
    fun getDoctorAvailable(@Query("preferredCity") preferredCity: String?, @Query("preferredLastName") preferredLastName: String?, @Query("preferredSpecialization") preferredSpecialization: String?): Call<List<Doctor>>

    @GET("/api/appointments")
    fun getAvailableAppointments(@Query("doctorId") doctorId: String?, @Query("nurseId") nurseId: String?, @Query("appointmentDate") appointmentDate: String?): Call<List<Appointment>>

    @GET("/api/patients")
    fun getPatients(@Header("Authorization") token: String, @Query("preferredEmail") preferredEmail: String?): Call<List<Patient>>

    @GET("/api/prescriptions/doctors/from-workplace")
    fun getDoctorsFromSameHospital(@Header("Authorization") token: String): Call<List<Doctor>>

    @GET("/api/prescriptions/nurses/from-workplace")
    fun getNursesFromSameHospital(@Header("Authorization") token: String): Call<List<Nurse>>
}