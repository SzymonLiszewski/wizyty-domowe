package com.medical.wizytydomowe.fragments.registerAppointment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointmentApi.AppointmentRetrofitInstance
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.appointments.AppointmentAdapter
import com.medical.wizytydomowe.api.authApi.AuthRetrofitInstance
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import com.medical.wizytydomowe.fragments.appointments.AppointmentDetailsFragment
import com.sahana.horizontalcalendar.HorizontalCalendar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchAppointmentFragment : Fragment(R.layout.search_appointment_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private lateinit var preferenceManager: PreferenceManager

    private var id: String? = null
    private var role: String? = null
    private var patientAddress: String? = null
    private var appointments: List<Appointment> = emptyList()

    private lateinit var errorConnectionView: MaterialCardView
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var horizontalCalendar: HorizontalCalendar
    private lateinit var noAppointmentView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorConnectionView = view.findViewById(R.id.errorConnectionView)
        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView)
        horizontalCalendar = view.findViewById(R.id.horizontalCalendar)
        noAppointmentView = view.findViewById(R.id.noAppointmentView)

        horizontalCalendar.setOnDateSelectListener { dateModel ->
            val selectedDate = "%04d-%02d-%02d".format(
                dateModel.year,
                dateModel.monthNumber,
                dateModel.day
            )

            if (role == "nurse") getAvailableAppointments(null, id, selectedDate)
            else getAvailableAppointments(id, null, selectedDate)
        }

        preferenceManager = PreferenceManager(requireContext())
        val token = preferenceManager.getAuthToken()

        id = arguments?.getSerializable("id") as String?
        role = arguments?.getSerializable("role") as String?

        if (token != null) sendUserInfoRequest(token)

        if (role == "nurse") getAvailableAppointments(null, id, null)
        else getAvailableAppointments(id, null, null)
    }

    private fun setNoAppointmentsLayout(){
        errorConnectionView.visibility = View.GONE
        appointmentsRecyclerView.visibility = View.GONE
        horizontalCalendar.visibility = View.VISIBLE
        noAppointmentView.visibility = View.VISIBLE
    }

    private fun setErrorConnectionLayout(){
        errorConnectionView.visibility = View.VISIBLE
        appointmentsRecyclerView.visibility = View.GONE
        horizontalCalendar.visibility = View.GONE
        noAppointmentView.visibility = View.GONE
    }

    private fun setAppointmentsLayout(){
        errorConnectionView.visibility = View.GONE
        appointmentsRecyclerView.visibility = View.VISIBLE
        horizontalCalendar.visibility = View.VISIBLE
        noAppointmentView.visibility = View.GONE

        recyclerView = appointmentsRecyclerView

        adapter = AppointmentAdapter(appointments) { appointment ->
            appointment.address = patientAddress
            navigateToAppointmentDetails(appointment)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun navigateToAppointmentDetails(appointment: Appointment){
        val bundle = Bundle().apply { putSerializable("appointment", appointment) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AppointmentDetailsFragment().apply { arguments = bundle })
    }

    private fun getAvailableAppointments(doctorId: String?, nurseId: String?, appointmentDate: String?){
        AppointmentRetrofitInstance.appointmentApiService.getAvailableAppointments(doctorId, nurseId, appointmentDate)
            .enqueue(object : Callback<List<Appointment>> {
                override fun onResponse(call: Call<List<Appointment>>, response: Response<List<Appointment>>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (!body.isNullOrEmpty()) {
                            appointments = body
                            setAppointmentsLayout()
                        }
                        else setNoAppointmentsLayout()
                    }
                    else setErrorConnectionLayout()
                }

                override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                    setErrorConnectionLayout()
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendUserInfoRequest(requestToken: String) {
        AuthRetrofitInstance.authApiService.getUserInfo(requestToken)
            .enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        patientAddress = responseBody?.address
                    } else Log.e("API", "Błąd: ${response.code()} - ${response.message()}")
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    Log.e("API", "Niepowodzenie: ${t.message}")
                }
            })
    }
}