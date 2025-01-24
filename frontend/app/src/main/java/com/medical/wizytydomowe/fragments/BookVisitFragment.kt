package com.medical.wizytydomowe.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.appointments.AppointmentAdapter
import com.medical.wizytydomowe.api.users.Doctor
import java.util.Calendar

class BookVisitFragment : Fragment(R.layout.book_visits_fragment) {

    private var doctor: Doctor? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private lateinit var appointmentsDisplayed: MutableList<Appointment>
    private lateinit var allAppointments: List<Appointment>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doctor = arguments?.getSerializable("doctor") as? Doctor

        val tvSelectedDate: TextView = view.findViewById(R.id.tvSelectedDate)
        val btnSelectDate: Button = view.findViewById(R.id.btnSelectDate)

        tvSelectedDate.text = "Wybrana data: -"

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
                patient = null,
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "24",
                status = "available",
                appointmentStartTime = "10.30 2025-06-01",
                appointmentEndTime = "10.45 2025-06-01",
                doctor = Doctor("2", "Jan", "Kowalski", "ginekolog", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "3",
                status = "available",
                appointmentStartTime = "10.15 2025-04-01",
                appointmentEndTime = "10.30 2025-04-01",
                doctor = Doctor("3", "Jan", "Kowalski", "urolog", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "4",
                status = "available",
                appointmentStartTime = "10.30 2025-03-01",
                appointmentEndTime = "10.45 2025-03-01",
                doctor = Doctor("4", "Jan", "Kowalski", "chirurg", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "2",
                status = "available",
                appointmentStartTime = "10.15 2025-02-01",
                appointmentEndTime = "10.30 2025-02-01",
                Doctor("5", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "1",
                status = "available",
                appointmentStartTime = "10.30 2025-01-01",
                appointmentEndTime = "10.45 2025-01-01",
                Doctor("6", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "12",
                status = "available",
                appointmentStartTime = "10.15 2024-12-01",
                appointmentEndTime = "10.30 2024-12-01",
                Doctor("7", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "14",
                status = "available",
                appointmentStartTime = "10.30 2024-12-12",
                appointmentEndTime = "10.45 2024-12-12",
                Doctor("8", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "456 Elm Street",
                notes = null
            ),
            Appointment(
                id = "123",
                status = "available",
                appointmentStartTime = "10.15 2025-06-01",
                appointmentEndTime = "10.30 2025-06-01",
                Doctor("9", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "123 Main Street",
                notes = null
            ),
            Appointment(
                id = "124",
                status = "available",
                appointmentStartTime = "10.30 2025-06-02",
                appointmentEndTime = "10.45 2025-06-02",
                Doctor("10", "Jan", "Kowalski", "psycholog", "Szpital Miejski w Gdańsku"),
                patient = null,
                address = "456 Elm Street",
                notes = null
            )
        )

        allAppointments = appointments
        appointmentsDisplayed = appointments.toMutableList()

        // Initialize adapter
        adapter = AppointmentAdapter(appointments) { appointment ->
            val preferenceManager = PreferenceManager(requireContext())
            if (preferenceManager.isLoggedIn()){
                val bundle = Bundle().apply {
                    putSerializable("appointment", appointment)
                }

                val bookVisitDetailsFragment = BookVisitDetailsFragment().apply {
                    arguments = bundle
                }
                val activity = activity as? FragmentNavigation
                activity?.navigateToFragment(bookVisitDetailsFragment)
            }
            else{
                val bookVisitLogoutFragment = BookVisitLogoutFragment()
                val activity = activity as? FragmentNavigation
                activity?.navigateToFragment(bookVisitLogoutFragment)
            }
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnSelectDate.setOnClickListener {
            showDatePicker { selectedDate ->
                tvSelectedDate.text = "Wybrana data: $selectedDate"
                filterAppointmentsByDate(selectedDate)
            }
        }

    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1, // Month is 0-based
                    selectedDay
                )
                onDateSelected(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun filterAppointmentsByDate(date: String) {
        appointmentsDisplayed = allAppointments.filter { it.appointmentStartTime.toString().substringAfter(" ") == date }.toMutableList()
        adapter.updateAppointments(appointmentsDisplayed)
    }

}