package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.medicalReports.MedicalReport
import com.medical.wizytydomowe.api.medicalReports.MedicalReportAdapter

class AvailableMedicalReportsFragment : Fragment(R.layout.available_medical_report_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicalReportAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rvMedicalReports)

        //TODO get medical reports

        val medicalReports = listOf(
            MedicalReport(id="1", status="dostępne", null, null, "2025-01-27", "Gdańsk 80-286 Jaśkowa Dolina 2137", "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                    "Uczucie duszności i zawroty głowy.\n" +
                    "Osoba osłabiona, blada, spocona."),
            MedicalReport(id="2", status="dostępne", null, null, "2025-01-25", "Gdańsk 80-286 Aleja Grunwaldzka 69", "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                    "Uczucie duszności i zawroty głowy.\n" +
                    "Osoba osłabiona, blada, spocona.")
        )

        // Initialize adapter
        adapter = MedicalReportAdapter(medicalReports) { medicalReport ->
            val bundle = Bundle().apply {
                putSerializable("medicalReport", medicalReport)
            }

            val medicalReportDetailsFragment = MedicalReportDetailsFragment().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(medicalReportDetailsFragment)
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}