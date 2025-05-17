package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.fragments.emergency.EmergencyDetailsFragment
import com.medical.wizytydomowe.fragments.emergency.EmergencyFragment

class ConfirmMedicalReportFragment : Fragment(R.layout.confirm_medical_report) {

    private var medicalReport: Emergency? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        medicalReport = arguments?.getSerializable("medicalReport") as? Emergency

        val noButton = view.findViewById<Button>(R.id.btnNo)
        val yesButton = view.findViewById<Button>(R.id.btnYes)

        noButton.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("medicalReport", medicalReport)
            }

            val emergencyDetailsFragment = EmergencyDetailsFragment().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(emergencyDetailsFragment)
        }

        yesButton.setOnClickListener{
            //TODO send request to save medical report
            Toast.makeText(context, "Podjęto się do zgłoszenia ${medicalReport?.id}", Toast.LENGTH_SHORT).show()
            val emergencyFragment = EmergencyFragment()
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(emergencyFragment)
        }
    }
}