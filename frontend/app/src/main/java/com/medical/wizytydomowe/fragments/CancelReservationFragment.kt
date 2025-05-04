package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.fragments.appointments.AppointmentDetails
import com.medical.wizytydomowe.fragments.appointments.AppointmentsFragment

class CancelReservationFragment : Fragment(R.layout.cancel_visit_reservation_fragment) {

    private var appointment: Appointment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointment = arguments?.getSerializable("appointment") as? Appointment

        val noButton = view.findViewById<Button>(R.id.btnNo)
        val yesButton = view.findViewById<Button>(R.id.btnYes)

        noButton.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("appointment", appointment)
            }

            val appointmentDetails = AppointmentDetails().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(appointmentDetails)
        }

        yesButton.setOnClickListener{
            //TODO send request to cancel reservation
            Toast.makeText(context, "Anulowano wizytę ${appointment?.id}", Toast.LENGTH_SHORT).show()
            val appointmentsFragment = AppointmentsFragment()
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(appointmentsFragment)
        }
    }
}