package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.appointments.AppointmentAdapter
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Patient

class VisitsFragment : Fragment(R.layout.visits_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rvAppointments)

        //TODO get user's appointments

        // Example data
        val appointments = listOf(
            Appointment(
                id = "23",
                status = "available",
                appointmentStartTime = "10.15 2025-05-01",
                appointmentEndTime = "10.30 2025-05-01",
                doctor = Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "24",
                status = "cancelled",
                appointmentStartTime = "10.30 2025-06-01",
                appointmentEndTime = "10.45 2025-06-01",
                doctor = Doctor("2", "Jan", "Kowalski", "ginekolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "3",
                status = "reserved",
                appointmentStartTime = "10.15 2025-04-01",
                appointmentEndTime = "10.30 2025-04-01",
                doctor = Doctor("3", "Jan", "Kowalski", "urolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "4",
                status = "reserved",
                appointmentStartTime = "10.30 2025-03-01",
                appointmentEndTime = "10.45 2025-03-01",
                doctor = Doctor("4", "Jan", "Kowalski", "chirurg", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "2",
                status = "completed",
                appointmentStartTime = "10.15 2025-02-01",
                appointmentEndTime = "10.30 2025-02-01",
                Doctor("5", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "1",
                status = "completed",
                appointmentStartTime = "10.30 2025-01-01",
                appointmentEndTime = "10.45 2025-01-01",
                Doctor("6", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "12",
                status = "cancelled",
                appointmentStartTime = "10.15 2024-12-01",
                appointmentEndTime = "10.30 2024-12-01",
                Doctor("7", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "14",
                status = "cancelled",
                appointmentStartTime = "10.30 2024-12-12",
                appointmentEndTime = "10.45 2024-12-12",
                Doctor("8", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "123",
                status = "reserved",
                appointmentStartTime = "10.15 2025-06-01",
                appointmentEndTime = "10.30 2025-06-01",
                Doctor("9", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "124",
                status = "completed",
                appointmentStartTime = "10.30 2025-06-02",
                appointmentEndTime = "10.45 2025-06-02",
                Doctor("10", "Jan", "Kowalski", "psycholog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                address = "456 Elm Street",
                notes = null
            )
        )

        // Initialize adapter
        adapter = AppointmentAdapter(appointments) { appointment ->
            val bundle = Bundle().apply {
                putSerializable("appointment", appointment)
            }

            val visitDetails = VisitDetails().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(visitDetails)
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}