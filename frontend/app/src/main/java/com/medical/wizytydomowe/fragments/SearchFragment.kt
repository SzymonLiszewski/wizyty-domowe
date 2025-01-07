package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.appointments.AppointmentAdapter
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Patient

class SearchFragment : Fragment(R.layout.search_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rvAppointments)
        val searchButton = view.findViewById<ImageButton>(R.id.imgbtnSearchAppointment)

        // Example data
        val appointments = listOf(
            Appointment(
                id = "123",
                status = "available",
                appointmentStartTime = "10.15 2025-06-01",
                appointmentEndTime = "10.30 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("1"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "124",
                status = "available",
                appointmentStartTime = "10.30 2025-06-01",
                appointmentEndTime = "10.45 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("2"),
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "123",
                status = "available",
                appointmentStartTime = "10.15 2025-06-01",
                appointmentEndTime = "10.30 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("1"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "124",
                status = "available",
                appointmentStartTime = "10.30 2025-06-01",
                appointmentEndTime = "10.45 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("2"),
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "123",
                status = "available",
                appointmentStartTime = "10.15 2025-06-01",
                appointmentEndTime = "10.30 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("1"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "124",
                status = "available",
                appointmentStartTime = "10.30 2025-06-01",
                appointmentEndTime = "10.45 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("2"),
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "123",
                status = "available",
                appointmentStartTime = "10.15 2025-06-01",
                appointmentEndTime = "10.30 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("1"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "124",
                status = "available",
                appointmentStartTime = "10.30 2025-06-01",
                appointmentEndTime = "10.45 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("2"),
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "123",
                status = "available",
                appointmentStartTime = "10.15 2025-06-01",
                appointmentEndTime = "10.30 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("1"),
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "124",
                status = "available",
                appointmentStartTime = "10.30 2025-06-01",
                appointmentEndTime = "10.45 2025-06-01",
                doctor = Doctor("1"),
                patient = Patient("2"),
                address = "456 Elm Street",
                notes = null
            )
        )

        // Initialize adapter
        adapter = AppointmentAdapter(appointments) { appointment ->
            Toast.makeText(requireContext(), "Booked: ${appointment.id}", Toast.LENGTH_SHORT).show()
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        searchButton.setOnClickListener{
            var searchQuery = view.findViewById<EditText>(R.id.et_search).text.toString()
            Toast.makeText(requireContext(), "Searched: ${searchQuery}", Toast.LENGTH_SHORT).show()
        }
    }


}