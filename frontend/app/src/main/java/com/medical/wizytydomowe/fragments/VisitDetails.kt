package com.medical.wizytydomowe.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment

class VisitDetails : Fragment(R.layout.visit_details) {

    private var appointment: Appointment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointment = arguments?.getSerializable("appointment") as? Appointment

        val tvStartDate: TextView = view.findViewById(R.id.tvStartDate)
        val tvEndDate: TextView = view.findViewById(R.id.tvEndDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnCancelAppointment: Button = view.findViewById(R.id.btnCancelAppointment)
        val tvDoctorName : TextView = view.findViewById(R.id.tvDoctorName)
        val tvDoctorSurname : TextView = view.findViewById(R.id.tvDoctorSurname)
        val tvDoctorSpeciality : TextView = view.findViewById(R.id.tvDoctorSpeciality)
        val tvPatientName : TextView = view.findViewById(R.id.tvPatientName)
        val tvPatientSurname : TextView = view.findViewById(R.id.tvPatientSurname)
        val tvDoctorHeader : TextView = view.findViewById(R.id.tvDoctorHeader)
        val tvPatientHeader : TextView = view.findViewById(R.id.tvPatientHeader)

        val preferenceManager = PreferenceManager(requireContext())


        //TODO patient can see doctor details (and nurse ???)
        //TODO doctor and nurse can see patient details ??? should they see each other if they went to home visit together ???
        if (preferenceManager.getRole() == "Patient"){
            tvPatientName.visibility = View.GONE
            tvPatientSurname.visibility = View.GONE
            tvPatientHeader.visibility = View.GONE
            tvDoctorName.visibility = View.VISIBLE
            tvDoctorSurname.visibility = View.VISIBLE
            tvDoctorHeader.visibility = View.VISIBLE
            tvDoctorSpeciality.visibility = View.VISIBLE
            tvDoctorName.text = "Imię: ${appointment?.doctor?.firstName}";
            tvDoctorSurname.text = "Nazwisko: ${appointment?.doctor?.lastName}";
            tvDoctorSpeciality.text = "Specjalność: ${appointment?.doctor?.specialization}";
        }
        else { // doctor and nurse
            tvPatientName.visibility = View.VISIBLE
            tvPatientSurname.visibility = View.VISIBLE
            tvPatientHeader.visibility = View.VISIBLE
            tvDoctorName.visibility = View.GONE
            tvDoctorSurname.visibility = View.GONE
            tvDoctorHeader.visibility = View.GONE
            tvDoctorSpeciality.visibility = View.GONE
            tvPatientName.text = "Imię: ${appointment?.patient?.firstName}";
            tvPatientSurname.text = "Nazwisko: ${appointment?.patient?.lastName}";
        }

        tvStartDate.text = "Data rozpoczęcia: ${appointment?.appointmentStartTime}";
        tvEndDate.text = "Data zakończenia: ${appointment?.appointmentEndTime}";

        when (appointment?.status.toString()) {
            "cancelled" -> tvStatus.text = "Anulowana"
            "completed" -> tvStatus.text = "Odbyta"
            "reserved" -> tvStatus.text = "Zarezerwowana"
            "available" -> tvStatus.text = "Dostępna"
        }

        when (tvStatus.text) {
            "Anulowana" -> tvStatus.setTextColor(Color.RED)
            "Odbyta" -> tvStatus.setTextColor(Color.BLACK)
            "Zarezerwowana" -> tvStatus.setTextColor(
                ContextCompat.getColor(view.context, R.color.gray)
            )
            else -> tvStatus.setTextColor(Color.BLACK)
        }


        btnCancelAppointment.visibility = if (tvStatus.text == "Zarezerwowana" && preferenceManager.getRole() == "Patient") {
            View.VISIBLE
        } else {
            View.GONE
        }

        tvStatus.text = "Status: ${tvStatus.text}"

        btnCancelAppointment.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("appointment", appointment)
            }

            val cancelReservationFragment = CancelReservationFragment().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(cancelReservationFragment)
        }

    }

}