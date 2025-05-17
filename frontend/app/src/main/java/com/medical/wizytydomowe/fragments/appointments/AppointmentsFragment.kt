package com.medical.wizytydomowe.fragments.appointments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.appointments.AppointmentAdapter
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Nurse
import com.medical.wizytydomowe.api.users.Patient
import com.medical.wizytydomowe.fragments.AddVisitFragment
import com.medical.wizytydomowe.fragments.SearchFragment
import com.medical.wizytydomowe.fragments.profile.LoginFragment

class AppointmentsFragment : Fragment(R.layout.appointments_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter

    private lateinit var preferenceManager : PreferenceManager
    private lateinit var logoutView: MaterialCardView
    private lateinit var noAppointmentView: MaterialCardView
    private lateinit var appointmentRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()
        val userRole = preferenceManager.getRole()

        logoutView = view.findViewById(R.id.logoutView)
        noAppointmentView = view.findViewById(R.id.noAppointmentView)
        appointmentRecyclerView = view.findViewById(R.id.appointmentsRecyclerView)

        val goToLoginButton = view.findViewById<Button>(R.id.goToLoginButton)
        val goToMakeAnAppointmentButton = view.findViewById<Button>(R.id.goToMakeAnAppointmentButton)
        val goToAddAppointmentButton = view.findViewById<Button>(R.id.goToAddAppointmentButton)

        goToLoginButton.setOnClickListener {
            navigateToLoginFragment()
        }

        goToMakeAnAppointmentButton.setOnClickListener {
            navigateToMakeAnAppointmentFragment()
        }

        goToAddAppointmentButton.setOnClickListener {
            navigateToAddAnAppointmentFragment()
        }

        //TODO get user's appointments from backend
        val appointments = listOf(
            Appointment(
                id = "23",
                status = "RESERVED",
                appointmentStartTime = "2025-06-01T10.15",
                appointmentEndTime = "2025-06-01T10.30",
                doctor = Doctor("1", "Marcin", "Rogowski", "Ginekolog", "Szpital Miejski w Warszawie"),
                nurse = Nurse("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                address = "Warszawa, 10-101, Opolska 12A/4",
                notes = "Płatność dostępna tylko za pomocą BLIK"
            ),
            Appointment(
                id = "24",
                status = "CANCELED",
                appointmentStartTime = "2024-06-01T10.15",
                appointmentEndTime = "2024-06-01T10.15",
                doctor = Doctor("2", "Jan", "Kowalski", "ginekolog", "Szpital Miejski w Opolu"),
                nurse = null,
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                address = "Opole, 10-101, Opolska 12A/4",
                notes = null
            ),
            Appointment(
                id = "3",
                status = "RESERVED",
                appointmentStartTime = "2024-06-01T09.15",
                appointmentEndTime = "2024-06-01T09.25",
                doctor = null,
                nurse = Nurse("1", "Agnieszka", "Jaworowicz", "Szpital Miejski we Wrocławiu"),
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                address = "Wrocław, 10-101, Opolska 12A/4",
                notes = null
            ),
            Appointment(
                id = "4",
                status = "RESERVED",
                appointmentStartTime = "10.30 2025-03-01",
                appointmentEndTime = "10.45 2025-03-01",
                doctor = Doctor("4", "Jan", "Kowalski", "chirurg", "Szpital Miejski w Gdańsku"),
                nurse = Nurse("1", "Agnieszka", "Jaworowicz", "Szpital Miejski we Wrocławiu"),
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                address = "456 Elm Street",
                notes = null
            )
        )

        if (userToken != null) {
            logoutView.visibility = View.GONE

            if (appointments.isEmpty()){
                setNoAppointmentsLayout(userRole, goToAddAppointmentButton, goToMakeAnAppointmentButton)
            }
            else{
                setAppointmentsLayout(appointments)
            }
        }
        else{
            setLogoutLayout()
        }
    }

    private fun setNoAppointmentsLayout(userRole: String?, goToAddAppointmentButton: Button,
                                        goToMakeAnAppointmentButton: Button){
        appointmentRecyclerView.visibility = View.GONE
        noAppointmentView.visibility = View.VISIBLE

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
    }

    private fun navigateToLoginFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(LoginFragment())
    }

    private fun navigateToMakeAnAppointmentFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(SearchFragment())
    }

    private fun navigateToAddAnAppointmentFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AddVisitFragment())
    }

    private fun navigateToAppointmentDetails(appointment: Appointment){
        val bundle = Bundle().apply { putSerializable("appointment", appointment) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AppointmentDetailsFragment().apply { arguments = bundle })
    }

}