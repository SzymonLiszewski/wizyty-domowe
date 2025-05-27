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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentsFragment : Fragment(R.layout.appointments_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter

    private lateinit var preferenceManager : PreferenceManager
    private lateinit var logoutView: MaterialCardView
    private lateinit var noAppointmentView: MaterialCardView
    private lateinit var errorConnectionView: MaterialCardView
    private lateinit var appointmentRecyclerView: RecyclerView

    private lateinit var goToMakeAnAppointmentButton: Button
    private lateinit var goToAddAppointmentButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()
        val userRole = preferenceManager.getRole()

        logoutView = view.findViewById(R.id.logoutView)
        noAppointmentView = view.findViewById(R.id.noAppointmentView)
        appointmentRecyclerView = view.findViewById(R.id.appointmentsRecyclerView)
        errorConnectionView = view.findViewById(R.id.errorConnectionView)

        val goToLoginButton = view.findViewById<Button>(R.id.goToLoginButton)
        goToMakeAnAppointmentButton = view.findViewById(R.id.goToMakeAnAppointmentButton)
        goToAddAppointmentButton = view.findViewById(R.id.goToAddAppointmentButton)

        goToLoginButton.setOnClickListener { navigateToLoginFragment() }
        goToMakeAnAppointmentButton.setOnClickListener { navigateToMakeAnAppointmentFragment() }
        goToAddAppointmentButton.setOnClickListener { navigateToAddAnAppointmentFragment() }

        if (userToken != null) {
            logoutView.visibility = View.GONE

            val token = "Bearer " + preferenceManager.getAuthToken()
            if (userRole == "Doctor") getDoctorAppointments(token)
            else if (userRole == "Patient") getPatientAppointments(token)
            else getNurseAppointments(token)
        }
        else{
            setLogoutLayout()
        }
    }

    private fun setNoAppointmentsLayout(userRole: String?, goToAddAppointmentButton: Button,
                                        goToMakeAnAppointmentButton: Button){
        appointmentRecyclerView.visibility = View.GONE
        noAppointmentView.visibility = View.VISIBLE
        errorConnectionView.visibility = View.GONE

        if (userRole == "Patient"){
            goToAddAppointmentButton.visibility = View.GONE
            goToMakeAnAppointmentButton.visibility = View.VISIBLE
        }
        else{
            goToAddAppointmentButton.visibility = View.VISIBLE
            goToMakeAnAppointmentButton.visibility = View.GONE
        }
    }

    private fun setAppointmentsLayout(appointments : List<Appointment>){
        appointmentRecyclerView.visibility = View.VISIBLE
        noAppointmentView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE

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
        noAppointmentView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE
    }

    private fun setErrorConnectionLayout(){
        appointmentRecyclerView.visibility = View.GONE
        noAppointmentView.visibility = View.GONE
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
                        setNoAppointmentsLayout(
                            preferenceManager.getRole(),
                            goToAddAppointmentButton,
                            goToMakeAnAppointmentButton
                        )
                    }
                } else setErrorConnectionLayout()
            }

            override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                setErrorConnectionLayout()
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getDoctorAppointments(token: String?) {
        val request = AppointmentRetrofitInstance.appointmentApiService.getDoctorAppointment(token.toString())
        getAppointments(request)
    }

    private fun getPatientAppointments(token: String?) {
        val request = AppointmentRetrofitInstance.appointmentApiService.getPatientAppointment(token.toString())
        getAppointments(request)
    }

    private fun getNurseAppointments(token: String?) {
        val request = AppointmentRetrofitInstance.appointmentApiService.getNurseAppointment(token.toString())
        getAppointments(request)
    }

}