package com.medical.wizytydomowe.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment

class BookVisitDetailsFragment : Fragment(R.layout.book_visit_details) {

    private var appointment: Appointment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointment = arguments?.getSerializable("appointment") as? Appointment

        val tvStartDate: TextView = view.findViewById(R.id.tvStartDate)
        val tvEndDate: TextView = view.findViewById(R.id.tvEndDate)
        val tvDoctorName : TextView = view.findViewById(R.id.tvDoctorName)
        val tvDoctorSurname : TextView = view.findViewById(R.id.tvDoctorSurname)
        val tvDoctorSpeciality : TextView = view.findViewById(R.id.tvDoctorSpeciality)
        val tvPatientName : TextView = view.findViewById(R.id.tvPatientName)
        val tvPatientSurname : TextView = view.findViewById(R.id.tvPatientSurname)
        val tvPatientAddress : TextView = view.findViewById(R.id.tvPatientAddress)
        val btnConfirmAppointment: Button = view.findViewById(R.id.btnConfirmAppointment)
        val btnReturn: Button = view.findViewById(R.id.btnReturn)

        tvStartDate.text = "Data rozpoczęcia: ${appointment?.appointmentStartTime}";
        tvEndDate.text = "Data rozpoczęcia: ${appointment?.appointmentEndTime}";
        tvDoctorName.text = "Imię: ${appointment?.doctor?.firstName}";
        tvDoctorSurname.text = "Nazwisko: ${appointment?.doctor?.lastName}";
        tvDoctorSpeciality.text = "Specjalność: ${appointment?.doctor?.specialization}";
        //TODO getUserInfo
        tvPatientName.text = "Imię: Test";
        tvPatientSurname.text = "Nazwisko: Testowy";
        tvPatientAddress.text = "Adres: UNKNOWN";


        btnConfirmAppointment.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("appointment", appointment)
            }

            val confirmVisitReservationFragment = ConfirmVisitReservationFragment().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(confirmVisitReservationFragment)
        }

        btnReturn.setOnClickListener{
            //TODO doctor as an argument
            val bundle = Bundle().apply {
                putSerializable("doctor", appointment?.doctor)
            }

            val bookVisitFragment = BookVisitFragment().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(bookVisitFragment)
        }

    }

}