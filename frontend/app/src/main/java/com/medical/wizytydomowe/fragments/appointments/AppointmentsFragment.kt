package com.medical.wizytydomowe.fragments.appointments

import android.os.Bundle
import android.view.View
import android.widget.Button
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
import com.medical.wizytydomowe.fragments.registerAppointment.SearchMedicalStaffFragment
import com.medical.wizytydomowe.fragments.profile.LoginFragment
import com.sahana.horizontalcalendar.HorizontalCalendar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentsFragment : Fragment(R.layout.appointments_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter

    private lateinit var preferenceManager : PreferenceManager
    private lateinit var logoutView: MaterialCardView
    private lateinit var noAppointmentPatientView: MaterialCardView
    private lateinit var errorConnectionView: MaterialCardView
    private lateinit var appointmentRecyclerView: RecyclerView
    private lateinit var horizontalCalendar: HorizontalCalendar
    private lateinit var noAppointmentMedicalStaffView: MaterialCardView

    private lateinit var goToMakeAnAppointmentButton: Button
    private lateinit var goToAddAppointmentButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()


        logoutView = view.findViewById(R.id.logoutView)
        noAppointmentPatientView = view.findViewById(R.id.noAppointmentPatientView)
        appointmentRecyclerView = view.findViewById(R.id.appointmentsRecyclerView)
        errorConnectionView = view.findViewById(R.id.errorConnectionView)
        horizontalCalendar = view.findViewById(R.id.horizontalCalendar)
        noAppointmentMedicalStaffView = view.findViewById(R.id.noAppointmentMedicalStaffView)

        val goToLoginButton = view.findViewById<Button>(R.id.goToLoginButton)
        goToMakeAnAppointmentButton = view.findViewById(R.id.goToMakeAnAppointmentButton)
        goToAddAppointmentButton = view.findViewById(R.id.goToAddAppointmentButton)

        goToLoginButton.setOnClickListener { navigateToLoginFragment() }
        goToMakeAnAppointmentButton.setOnClickListener { navigateToMakeAnAppointmentFragment() }
        goToAddAppointmentButton.setOnClickListener { navigateToAddAnAppointmentFragment() }

        horizontalCalendar.setOnDateSelectListener { dateModel ->
            val selectedDate = "%04d-%02d-%02d".format(
                dateModel.year,
                dateModel.monthNumber,
                dateModel.day
            )

            sendRequestForAppointments(selectedDate)
        }

        if (userToken != null) {
            logoutView.visibility = View.GONE

            sendRequestForAppointments(null)
        }
        else{
            setLogoutLayout()
        }
    }

    private fun sendRequestForAppointments(selectedDate: String?){
        val userRole = preferenceManager.getRole()
        val token = "Bearer " + preferenceManager.getAuthToken()
        if (userRole == "Doctor") getDoctorAppointments(token, selectedDate)
        else if (userRole == "Patient") getPatientAppointments(token)
        else getNurseAppointments(token, selectedDate)
    }

    private fun setNoAppointmentsLayout(userRole: String?){
        appointmentRecyclerView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE

        if (userRole == "Patient"){
            noAppointmentPatientView.visibility = View.VISIBLE
            horizontalCalendar.visibility = View.GONE
            noAppointmentMedicalStaffView.visibility = View.GONE
        }
        else{
            noAppointmentPatientView.visibility = View.GONE
            horizontalCalendar.visibility = View.VISIBLE
            noAppointmentMedicalStaffView.visibility = View.VISIBLE
        }
    }

    private fun setAppointmentsLayout(appointments : List<Appointment>){
        appointmentRecyclerView.visibility = View.VISIBLE
        errorConnectionView.visibility = View.GONE
        noAppointmentPatientView.visibility = View.GONE
        noAppointmentMedicalStaffView.visibility = View.GONE

        if (preferenceManager.getRole() == "Patient") horizontalCalendar.visibility = View.GONE
        else horizontalCalendar.visibility = View.VISIBLE

        recyclerView = appointmentRecyclerView

        adapter = AppointmentAdapter(appointments) { appointment ->
            navigateToAppointmentDetails(appointment)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun setLogoutLayout(){
        logoutView.visibility = View.VISIBLE
        appointmentRecyclerView.visibility = View.GONE
        noAppointmentPatientView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE
        horizontalCalendar.visibility = View.GONE
        noAppointmentMedicalStaffView.visibility = View.GONE
    }

    private fun setErrorConnectionLayout(){
        logoutView.visibility = View.VISIBLE
        appointmentRecyclerView.visibility = View.GONE
        noAppointmentPatientView.visibility = View.GONE
        horizontalCalendar.visibility = View.GONE
        noAppointmentMedicalStaffView.visibility = View.GONE
        errorConnectionView.visibility = View.VISIBLE
    }

    private fun navigateToLoginFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(LoginFragment())
    }

    private fun navigateToMakeAnAppointmentFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(SearchMedicalStaffFragment())
    }

    private fun navigateToAddAnAppointmentFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AddAppointmentFragment())
    }

    private fun navigateToAppointmentDetails(appointment: Appointment){
        val bundle = Bundle().apply { putSerializable("appointment", appointment) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AppointmentDetailsFragment().apply { arguments = bundle })
    }

    private fun getAppointments(request: Call<List<Appointment>>) {
        request.enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(call: Call<List<Appointment>>, response: Response<List<Appointment>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (!body.isNullOrEmpty()) setAppointmentsLayout(body)
                    else {
                        setNoAppointmentsLayout(preferenceManager.getRole())
                    }
                } else setErrorConnectionLayout()
            }

            override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                setErrorConnectionLayout()
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getDoctorAppointments(token: String?, selectedDate: String?) {
        val request = AppointmentRetrofitInstance.appointmentApiService.getDoctorAppointment(token.toString(), selectedDate)
        getAppointments(request)
    }

    private fun getPatientAppointments(token: String?) {
        val request = AppointmentRetrofitInstance.appointmentApiService.getPatientAppointment(token.toString())
        getAppointments(request)
    }

    private fun getNurseAppointments(token: String?, selectedDate: String?) {
        val request = AppointmentRetrofitInstance.appointmentApiService.getNurseAppointment(token.toString(), selectedDate)
        getAppointments(request)
    }

}