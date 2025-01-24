package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.prescriptions.Prescription
import com.medical.wizytydomowe.api.prescriptions.PrescriptionAdapter
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Patient

class PrescriptionsFragment : Fragment(R.layout.prescriptions_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrescriptionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rvPrescriptions)

        //TODO get user's prescriptions

        // Example data
        val prescriptions = listOf(
            Prescription(
                id = "1",
                prescriptionTime = "2025-05-01",
                doctor = Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            ),
            Prescription(
                id = "2",
                prescriptionTime = "2025-04-01",
                doctor = Doctor("2", "Jan", "Kowalski", "ginekolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            ),
            Prescription(
                id = "3",
                prescriptionTime = "2025-03-01",
                doctor = Doctor("3", "Jan", "Kowalski", "urolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            ),
            Prescription(
                id = "4",
                prescriptionTime = "2025-02-01",
                doctor = Doctor("4", "Jan", "Kowalski", "chirurg", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            ),
            Prescription(
                id = "5",
                prescriptionTime = "2025-05-11",
                doctor = Doctor("5", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            ),
            Prescription(
                id = "6",
                prescriptionTime = "2025-05-11",
                doctor = Doctor("6", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            ),
            Prescription(
                id = "7",
                prescriptionTime = "2025-05-14",
                doctor = Doctor("7", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            ),
            Prescription(
                id = "8",
                prescriptionTime = "2025-05-21",
                doctor = Doctor("8", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            ),
            Prescription(
                id = "9",
                prescriptionTime = "2025-05-22",
                doctor = Doctor("9", "Jan", "Kowalski", "pediatra", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski"),
                notes = "Przepisane leki:\n" +
                        "\n" +
                        "Amoksycylina 500 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 kapsułka co 8 godzin\n" +
                        "Ilość: 30 kapsułek\n" +
                        "Sposób stosowania: Doustnie, po posiłku\n" +
                        "Ibuprofen 200 mg\n" +
                        "\n" +
                        "Dawkowanie: 1 tabletka co 6 godzin w razie bólu\n" +
                        "Ilość: 20 tabletek\n" +
                        "Sposób stosowania: Doustnie, popić wodą\n" +
                        "Zalecenia dodatkowe:\n" +
                        "\n" +
                        "Unikać spożywania alkoholu podczas stosowania leków.\n" +
                        "W przypadku wystąpienia działań niepożądanych skontaktować się z lekarzem."
            )
        )

        // Initialize adapter
        adapter = PrescriptionAdapter(prescriptions) { prescription ->
            val bundle = Bundle().apply {
                putSerializable("prescription", prescription)
            }

            val presccriptionDetails = PrescriptionDetailsFragment().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(presccriptionDetails)
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

}