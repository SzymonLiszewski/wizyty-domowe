package com.medical.wizytydomowe.fragments.emergency

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.emergency.EmergencyAdapter
import com.medical.wizytydomowe.api.users.Paramedic
import com.medical.wizytydomowe.api.users.Patient

class EmergencyFragment  : Fragment(R.layout.emergency_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmergencyAdapter

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var noEmergencyView: MaterialCardView
    private lateinit var emergencyRecyclerView: RecyclerView
    private lateinit var goToAddEmergencyButton: Button
    private lateinit var goToAvailableEmergencyButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()

        emergencyRecyclerView = view.findViewById(R.id.emergencyRecyclerView)
        noEmergencyView = view.findViewById(R.id.noEmergencyView)
        goToAvailableEmergencyButton = view.findViewById(R.id.goToAvailableEmergencyButton)
        goToAddEmergencyButton = view.findViewById(R.id.goToAddEmergencyButton)

        goToAvailableEmergencyButton.setOnClickListener {
            navigateToAvailableFragment()
        }

        goToAddEmergencyButton.setOnClickListener {
            navigateToAddEmergencyFragment()
        }

        //TODO get medical reports
        val emergencies = listOf(
            Emergency(id="1",
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "IN PROGRESS",
                date="2025-06-01T10.15",
                address = "Warszawa, 10-101, Opolska 12A/4",
                description = "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
            Emergency(id="2",
                patient = Patient("1", "Jan", "Rogowski", "jr@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "COMPLETED",
                date="2025-06-02T11.15",
                address = "Gdańsk, 10-101, Gdańska 12A/4",
                description = "Bardzo silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
            Emergency(id="3",
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "COMPLETED",
                date="2025-06-01T10.15",
                address = "Warszawa, 10-101, Opolska 12A/4",
                description = "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
            Emergency(id="4",
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "COMPLETED",
                date="2025-06-01T10.15",
                address = "Warszawa, 10-101, Opolska 12A/4",
                description = "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
            Emergency(id="5",
                patient = Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
                paramedic = Paramedic("1", "Agnieszka", "Jaworowicz", "Szpital Miejski w Warszawie"),
                status = "COMPLETED",
                date="2025-06-01T10.15",
                address = "Warszawa, 10-101, Opolska 12A/4",
                description = "Silne bóle w klatce piersiowej trwające od około 30 minut.\n" +
                        "Uczucie duszności i zawroty głowy.\n" +
                        "Osoba osłabiona, blada, spocona."),
        )

        if (emergencies.size == 0) setNoEmergenciesLayout(userRole)
        else setEmergenciesLayout(emergencies)
    }

    private fun navigateToEmergencyDetailsFragment(emergency: Emergency){
        val bundle = Bundle().apply { putSerializable("emergency", emergency) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyDetailsFragment().apply { arguments = bundle })
    }

    private fun navigateToAvailableFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyAvailableFragment())
    }

    private fun setNoEmergenciesLayout(userRole: String?){
        emergencyRecyclerView.visibility = View.GONE
        noEmergencyView.visibility = View.VISIBLE

        if (userRole == "Patient"){
            goToAddEmergencyButton.visibility = View.VISIBLE
            goToAvailableEmergencyButton.visibility = View.GONE
        }
        else{
            goToAddEmergencyButton.visibility = View.GONE
            goToAvailableEmergencyButton.visibility = View.VISIBLE
        }
    }

    private fun setEmergenciesLayout(emergencies: List<Emergency>){
        emergencyRecyclerView.visibility = View.VISIBLE
        noEmergencyView.visibility = View.GONE

        recyclerView = emergencyRecyclerView

        adapter = EmergencyAdapter(emergencies) { emergency ->
            navigateToEmergencyDetailsFragment(emergency)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun navigateToAddEmergencyFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AddEmergencyFragment())
    }
}