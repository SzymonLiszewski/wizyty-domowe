package com.medical.wizytydomowe.fragments.emergency

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.emergency.EmergencyAdapter
import com.medical.wizytydomowe.api.users.Paramedic
import com.medical.wizytydomowe.api.users.Patient

class EmergencyAvailableFragment : Fragment(R.layout.emergency_available_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmergencyAdapter

    private lateinit var noEmergencyView: MaterialCardView
    private lateinit var emergencyRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emergencyRecyclerView = view.findViewById(R.id.emergencyRecyclerView)
        noEmergencyView = view.findViewById(R.id.noEmergencyView)

        //TODO get available emergencies from backend

        val emergencies = listOf(
            Emergency(id="1",
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "AVAILABLE",
                date="2025-06-01T10.15",
                address = "Warszawa, 10-101, Opolska 12A/4",
                description = "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
            Emergency(id="2",
                patient = Patient("1", "Jan", "Rogowski", "jr@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "AVAILABLE",
                date="2025-06-02T11.15",
                address = "Gdańsk, 10-101, Gdańska 12A/4",
                description = "Bardzo silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
            Emergency(id="3",
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "AVAILABLE",
                date="2025-06-01T10.15",
                address = "Warszawa, 10-101, Opolska 12A/4",
                description = "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
            Emergency(id="4",
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "AVAILABLE",
                date="2025-06-01T10.15",
                address = "Warszawa, 10-101, Opolska 12A/4",
                description = "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
            Emergency(id="5",
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "AVAILABLE",
                date="2025-06-01T10.15",
                address = "Warszawa, 10-101, Opolska 12A/4",
                description = "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
        )

        if (emergencies.size == 0) setNoAvailableEmergencyLayout()
        else setEmergencyLayout(emergencies)
    }

    private fun setNoAvailableEmergencyLayout(){
        noEmergencyView.visibility = View.VISIBLE
        emergencyRecyclerView.visibility = View.GONE
    }

    private fun setEmergencyLayout(emergencies: List<Emergency>){
        noEmergencyView.visibility = View.GONE
        emergencyRecyclerView.visibility = View.VISIBLE

        recyclerView = emergencyRecyclerView

        adapter = EmergencyAdapter(emergencies) { emergency ->
            navigateToEmergencyDetailsFragment(emergency)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun navigateToEmergencyDetailsFragment(emergency: Emergency){
        val bundle = Bundle().apply { putSerializable("emergency", emergency) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyDetailsFragment().apply { arguments = bundle })
    }
}