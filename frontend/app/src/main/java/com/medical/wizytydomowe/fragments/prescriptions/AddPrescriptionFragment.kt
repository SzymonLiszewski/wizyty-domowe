package com.medical.wizytydomowe.fragments.prescriptions

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.medication.Medication
import com.medical.wizytydomowe.api.medication.MedicationAdapter
import com.medical.wizytydomowe.api.prescriptions.Prescription
import com.medical.wizytydomowe.api.users.Patient
import com.medical.wizytydomowe.api.users.User
import com.medical.wizytydomowe.api.users.UserAdapter
import com.medical.wizytydomowe.api.utils.*

class AddPrescriptionFragment : Fragment(R.layout.add_prescription_fragment)  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var medicationAdapter: MedicationAdapter

    private var pageNumber: Int = 1
    private lateinit var users: List<User>
    private lateinit var medications: MutableList<Medication>
    private lateinit var selectedPatient: Patient

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var filterPatientView: MaterialCardView
    private lateinit var goToThePage3View: MaterialCardView
    private lateinit var addMedicationView: MaterialCardView
    private lateinit var medicationRecyclerView: RecyclerView
    private lateinit var notesView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageNumber = 1

        userRecyclerView = view.findViewById(R.id.userRecyclerView)
        filterPatientView = view.findViewById(R.id.filterPatientView)
        goToThePage3View = view.findViewById(R.id.goToThePage3View)
        addMedicationView = view.findViewById(R.id.addMedicationView)
        medicationRecyclerView = view.findViewById(R.id.medicationRecyclerView)
        notesView = view.findViewById(R.id.notesView)

        val filterPatientButton = view.findViewById<Button>(R.id.filterPatientButton)
        val addMedicationButton = view.findViewById<Button>(R.id.addMedicationButton)
        val buttonPrevPage3 = view.findViewById<Button>(R.id.buttonPrevPage3)
        val buttonAddPrescription = view.findViewById<Button>(R.id.buttonAddPrescription)

        filterPatientButton.setOnClickListener { filterPatients() }

        addMedicationButton.setOnClickListener { addMedication() }
        goToThePage3View.setOnClickListener {
            pageNumber += 1
            setPage(pageNumber)
        }

        buttonPrevPage3.setOnClickListener {
            pageNumber -= 1
            setPage(pageNumber)
        }

        buttonAddPrescription.setOnClickListener { addPrescription() }

        medications = mutableListOf()
        //TODO get patient list from backend
        users = listOf(
            Patient("1", "Robert", "Kozłowski", "j@2gmail.com", "123-456-789"),
            Patient("1", "Robert", "Stanowski", "j@2gmail.com", "123-456-789"),
            Patient("1", "Maciej", "Maciak", "j@2gmail.com", "123-456-789"),
            Patient("1", "Szewczyk", "Dratewka", "j@2gmail.com", "123-456-789"),
            Patient("1", "Jędrzej", "Stanowski", "j@2gmail.com", "123-456-789")
        )

        setPage(1)
    }

    private fun setPatientRecyclerView(){
        recyclerView = userRecyclerView

        userAdapter = UserAdapter(users) { user ->
            if (user is Patient) selectedPatient = user
            pageNumber += 1
            setPage(pageNumber)
            Log.d("pageNumber", pageNumber.toString())
        }

        recyclerView.adapter = userAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun setPage(pageNumber: Int){
        if (pageNumber == 1) setPage1Layout()
        if (pageNumber == 2) setPage2Layout()
        if (pageNumber == 3) setPage3Layout()
    }

    private fun setPage1Layout(){
        userRecyclerView.visibility = View.VISIBLE
        filterPatientView.visibility = View.VISIBLE
        addMedicationView.visibility = View.GONE
        medicationRecyclerView.visibility = View.GONE
        goToThePage3View.visibility = View.GONE
        notesView.visibility = View.GONE

        //TODO send request to the backend for patient list
        setPatientRecyclerView()
    }

    private fun setPage2Layout(){
        userRecyclerView.visibility = View.GONE
        filterPatientView.visibility = View.GONE
        addMedicationView.visibility = View.VISIBLE
        medicationRecyclerView.visibility = View.VISIBLE
        goToThePage3View.visibility = View.GONE
        notesView.visibility = View.GONE

        setMedicationsRecyclerView()
        checkGoToPage3ViewVisibility()
    }

    private fun setPage3Layout(){
        userRecyclerView.visibility = View.GONE
        filterPatientView.visibility = View.GONE
        addMedicationView.visibility = View.GONE
        medicationRecyclerView.visibility = View.GONE
        goToThePage3View.visibility = View.GONE
        notesView.visibility = View.VISIBLE

    }

    private fun filterPatients(){
        val textInputLayoutPatientFilter = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPatientFilter)
        val textInputEditTextPatientFilter = view?.findViewById<TextInputEditText>(R.id.textInputEditTextPatientFilter)?.text.toString()

        if (validateFilterText(textInputEditTextPatientFilter, textInputLayoutPatientFilter)){
            //TODO send request to the backend to the filter patients by e-mail, firstName or lastName
        }
    }

    private fun setMedicationsRecyclerView(){
        recyclerView = medicationRecyclerView

        medicationAdapter = MedicationAdapter(medications) { medication ->
            deleteMedication(medication)
        }

        recyclerView.adapter = medicationAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun addMedication(){
        val textInputLayoutMedicationName = view?.findViewById<TextInputLayout>(R.id.textInputLayoutMedicationName)
        val medicationName = view?.findViewById<TextInputEditText>(R.id.textInputEditTextMedicationName)?.text.toString()
        val textInputLayoutDosages = view?.findViewById<TextInputLayout>(R.id.textInputLayoutDosages)
        val medicationDosage = view?.findViewById<TextInputEditText>(R.id.textInputEditTextDosages)?.text.toString()

        if (validateNewMedication(medicationName, medicationDosage, textInputLayoutMedicationName, textInputLayoutDosages)){
            medications.add(Medication(medicationName, medicationDosage))
            medicationAdapter.updateMedications(medications)
            checkGoToPage3ViewVisibility()
            resetAddMedicationForm()
        }
    }

    private fun deleteMedication(medication: Medication){
        medications.remove(medication)
        medicationAdapter.updateMedications(medications)
        checkGoToPage3ViewVisibility()
    }

    private fun checkGoToPage3ViewVisibility(){
        if (medications.size > 0) goToThePage3View.visibility = View.VISIBLE
        else goToThePage3View.visibility = View.GONE
    }

    private fun resetAddMedicationForm(){
        view?.findViewById<TextInputEditText>(R.id.textInputEditTextMedicationName)?.text?.clear()
        view?.findViewById<TextInputEditText>(R.id.textInputEditTextDosages)?.text?.clear()
    }

    private fun addPrescription(){
        val textInputLayoutNotes = view?.findViewById<TextInputLayout>(R.id.textInputLayoutNotes)
        val notes = view?.findViewById<TextInputEditText>(R.id.textInputEditTextNotes)?.text.toString()

        if (validateDescription(notes, textInputLayoutNotes)){
            val medication = medications.joinToString(";") { it.name }
            val dosage = medications.joinToString(";") { it.dosage }
            val newPrescription = Prescription("0", null, selectedPatient, setActualDate(), medication, dosage, notes)
            navigateToPrescriptionDetailsFragment(newPrescription)
        }
    }

    private fun navigateToPrescriptionDetailsFragment(prescription: Prescription){
        val bundle = Bundle().apply {
            putSerializable("prescription", prescription)
            putSerializable("addNewPrescriptionFlag", true)
        }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(PrescriptionDetailsFragment().apply { arguments = bundle })
    }
}