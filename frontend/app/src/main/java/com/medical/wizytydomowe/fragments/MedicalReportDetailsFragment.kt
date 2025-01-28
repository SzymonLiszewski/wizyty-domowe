package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.medicalReports.MedicalReport

class MedicalReportDetailsFragment : Fragment(R.layout.medical_report_details_fragment) {

    private var medicalReport: MedicalReport? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        medicalReport = arguments?.getSerializable("medicalReport") as? MedicalReport

        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnSubmit: Button = view.findViewById(R.id.btnSubmit)

        tvDate.text = "Data: ${medicalReport?.date}"
        tvAddress.text = "Adres: ${medicalReport?.address}"
        tvStatus.text = "Status: ${medicalReport?.status}"
        tvDescription.text = "${medicalReport?.description}"

        if (tvStatus.text.toString() == "Status: dostępne")  {
            btnSubmit.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable("medicalReport", medicalReport)
                }

                val confirmMedicalReportFragment = ConfirmMedicalReportFragment().apply {
                    arguments = bundle
                }
                val activity = activity as? FragmentNavigation
                activity?.navigateToFragment(confirmMedicalReportFragment)
            }
        }
        else{
            btnSubmit.visibility = View.GONE
        }

    }


}