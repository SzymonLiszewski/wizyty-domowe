package com.medical.wizytydomowe.fragments.prescriptions

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.prescriptions.Prescription
import com.medical.wizytydomowe.api.prescriptions.PrescriptionAdapter
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Patient
import com.medical.wizytydomowe.fragments.SearchFragment
import com.medical.wizytydomowe.fragments.profile.LoginFragment

class PrescriptionsFragment : Fragment(R.layout.prescriptions_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrescriptionAdapter

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var logoutView: MaterialCardView
    private lateinit var noPrescriptionView: MaterialCardView
    private lateinit var prescriptionsRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()
        val userToken = preferenceManager.getAuthToken()

        logoutView = view.findViewById(R.id.logoutView)
        noPrescriptionView = view.findViewById(R.id.noPrescriptionView)
        prescriptionsRecyclerView = view.findViewById(R.id.prescriptionsRecyclerView)

        val goToLoginButton = view.findViewById<Button>(R.id.goToLoginButton)
        val goToAddPrescriptionButton = view.findViewById<Button>(R.id.goToAddPrescriptionButton)
        val goToMakeAnAppointmentButton = view.findViewById<Button>(R.id.goToMakeAnAppointmentButton)

        goToLoginButton.setOnClickListener {
            navigateToLoginFragment()
        }

        goToAddPrescriptionButton.setOnClickListener {
            navigateToAddPrescriptionFragment()
        }

        goToMakeAnAppointmentButton.setOnClickListener {
            navigateToMakeAnAppointmentFragment()
        }



        //TODO get user's prescriptions from backend
        val prescriptions = listOf(
            Prescription(
                id = "1",
                doctor = Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Rupert", "Kozłowski", "ab@a.pl", "123456789"),
                date = "2025-05-01T13:30",
                medication = "Paracetamol;Amoxicillin;Ibuprofen",
                dosage = "1 tabletka co 6 godzin, maksymalnie 4 tabletki na dobę.;1 kapsułka 3 razy dziennie przez 7 dni.;1–2 tabletki co 6–8 godzin w razie bólu, nie więcej niż 6 tabletek na dobę.",
                notes = "Receptę należy wykupić w przeciągu najbliższych 12 miesięcy w dowolnej aptece, okazując swój dowód osobisty."
            ),
            Prescription(
                id = "2",
                doctor = Doctor("1", "Marcin", "Rogowski", "ginekolog", "Szpital Miejski we Wrocławiu"),
                patient = Patient("1", "Robert", "Kozłowski", "a@a.pl", "123456789"),
                date = "2023-05-02T18:30",
                medication = "Paracetamol;Amoxicillin;Ibuprofen",
                dosage = "1 tabletka co 6 godzin, maksymalnie 4 tabletki na dobę.;1 kapsułka 3 razy dziennie przez 7 dni.;1–2 tabletki co 6–8 godzin w razie bólu, nie więcej niż 6 tabletek na dobę.",
                notes = "Receptę należy wykupić w przeciągu najbliższych 12 miesięcy w dowolnej aptece, okazując swój dowód osobisty."
            ),
            Prescription(
                id = "3",
                doctor = Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski", "a", "a"),
                date = "2025-05-01T13:30",
                medication = "Paracetamol;Amoxicillin;Ibuprofen",
                dosage = "1 tabletka co 6 godzin, maksymalnie 4 tabletki na dobę.;1 kapsułka 3 razy dziennie przez 7 dni.;1–2 tabletki co 6–8 godzin w razie bólu, nie więcej niż 6 tabletek na dobę.",
                notes = "Receptę należy wykupić w przeciągu najbliższych 12 miesięcy w dowolnej aptece, okazując swój dowód osobisty."
            ),
            Prescription(
                id = "4",
                doctor = Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski", "a", "a"),
                date = "2025-05-01T13:30",
                medication = "Paracetamol;Amoxicillin;Ibuprofen",
                dosage = "1 tabletka co 6 godzin, maksymalnie 4 tabletki na dobę.;1 kapsułka 3 razy dziennie przez 7 dni.;1–2 tabletki co 6–8 godzin w razie bólu, nie więcej niż 6 tabletek na dobę.",
                notes = "Receptę należy wykupić w przeciągu najbliższych 12 miesięcy w dowolnej aptece, okazując swój dowód osobisty."
            ),
            Prescription(
                id = "4",
                doctor = Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
                patient = Patient("1", "Robert", "Kozłowski", "a", "a"),
                date = "2025-05-01T13:30",
                medication = "Paracetamol;Amoxicillin;Ibuprofen",
                dosage = "1 tabletka co 6 godzin, maksymalnie 4 tabletki na dobę.;1 kapsułka 3 razy dziennie przez 7 dni.;1–2 tabletki co 6–8 godzin w razie bólu, nie więcej niż 6 tabletek na dobę.",
                notes = "Receptę należy wykupić w przeciągu najbliższych 12 miesięcy w dowolnej aptece, okazując swój dowód osobisty."
            )
        )

        if (userToken != null) {
            logoutView.visibility = View.GONE

            if (prescriptions.isEmpty()){
                setNoPrescriptionsLayout(userRole, goToAddPrescriptionButton, goToMakeAnAppointmentButton)
            }
            else{
                setPrescriptionsLayout(prescriptions)
            }
        }
        else{
            setLogoutLayout()
        }
    }

    private fun setPrescriptionsLayout(prescriptions: List<Prescription>){
        prescriptionsRecyclerView.visibility = View.VISIBLE
        noPrescriptionView.visibility = View.GONE

        recyclerView = prescriptionsRecyclerView

        adapter = PrescriptionAdapter(prescriptions) { prescription ->
            navigateToPrescriptionDetailsFragment(prescription)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setNoPrescriptionsLayout(userRole: String?, goToAddPrescriptionButton: Button,
                                         goToMakeAnAppointmentButton: Button){
        noPrescriptionView.visibility = View.VISIBLE
        prescriptionsRecyclerView.visibility = View.GONE

        if (userRole == "Patient"){
            goToMakeAnAppointmentButton.visibility = View.VISIBLE
            goToAddPrescriptionButton.visibility = View.GONE
        }
        else{
            goToMakeAnAppointmentButton.visibility = View.GONE
            goToAddPrescriptionButton.visibility = View.VISIBLE
        }
    }

    private fun setLogoutLayout(){
        logoutView.visibility = View.VISIBLE
        noPrescriptionView.visibility = View.GONE
        prescriptionsRecyclerView.visibility = View.GONE
    }

    private fun navigateToLoginFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(LoginFragment())
    }

    private fun navigateToMakeAnAppointmentFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(SearchFragment())
    }

    private fun navigateToAddPrescriptionFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AddPrescriptionFragment())
    }

    private fun navigateToPrescriptionDetailsFragment(prescription: Prescription){
        val bundle = Bundle().apply { putSerializable("prescription", prescription) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(PrescriptionDetailsFragment().apply { arguments = bundle })
    }

}