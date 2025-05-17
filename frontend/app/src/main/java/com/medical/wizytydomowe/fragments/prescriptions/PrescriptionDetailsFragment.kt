package com.medical.wizytydomowe.fragments.prescriptions

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.prescriptions.Prescription
import com.medical.wizytydomowe.api.utils.*

class PrescriptionDetailsFragment : Fragment(R.layout.prescription_details) {

    private var prescription: Prescription? = null
    private lateinit var preferenceManager: PreferenceManager
    private var addNewPrescriptionFlag: Boolean? = null

    private lateinit var patientView: MaterialCardView
    private lateinit var doctorView: MaterialCardView
    private lateinit var prescriptionDateView: MaterialCardView
    private lateinit var medicationView: MaterialCardView
    private lateinit var notesView: MaterialCardView
    private lateinit var addPrescriptionView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prescription = arguments?.getSerializable("prescription") as? Prescription
        addNewPrescriptionFlag = arguments?.getSerializable("addNewPrescriptionFlag") as? Boolean

        patientView = view.findViewById(R.id.patientView)
        doctorView = view.findViewById(R.id.doctorView)
        prescriptionDateView = view.findViewById(R.id.prescriptionDateView)
        medicationView = view.findViewById(R.id.medicationView)
        notesView = view.findViewById(R.id.notesView)
        addPrescriptionView = view.findViewById(R.id.addPrescriptionView)

        addPrescriptionView.setOnClickListener {
            Toast.makeText(requireContext(), "Dodano receptę", Toast.LENGTH_SHORT).show()
            //TODO send request to the backend and add dialog
        }

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()

        if (addNewPrescriptionFlag != null && addNewPrescriptionFlag == true) addPrescriptionView.visibility = View.VISIBLE
        else addPrescriptionView.visibility = View.GONE

        if (userRole == "Patient") setPatientLayout()
        else setDoctorLayout()
    }

    private fun setPatientLayout(){
        doctorView.visibility = View.VISIBLE
        patientView.visibility = View.GONE

        setMainView()
        setDoctorView()
    }

    private fun setDoctorLayout(){
        doctorView.visibility = View.GONE
        patientView.visibility = View.VISIBLE

        setMainView()
        setPatientView()
    }

    private fun setPatientView(){
        setPatientData()
    }

    private fun setPatientData(){
        view?.findViewById<TextView>(R.id.firstNamePatientTextView)?.text = "${prescription?.patient?.firstName}"
        view?.findViewById<TextView>(R.id.lastNamePatientTextView)?.text = "${prescription?.patient?.lastName}"
        view?.findViewById<TextView>(R.id.phoneNumberPatientTextView)?.text = "123-456-789"
        view?.findViewById<TextView>(R.id.emailPatientTextView)?.text = "janrogowski@gmail.com"
    }

    private fun setDoctorView(){
        setDoctorData()
    }

    private fun setDoctorData(){
        view?.findViewById<TextView>(R.id.firstNameDoctorTextView)?.text = "${prescription?.doctor?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameDoctorTextView)?.text = "${prescription?.doctor?.lastName}"
        view?.findViewById<TextView>(R.id.specializationDoctorTextView)?.text = "${prescription?.doctor?.specialization}"
        view?.findViewById<TextView>(R.id.hospitalDoctorTextView)?.text = "${prescription?.doctor?.workPlace}"
    }

    private fun setMainView(){
        setPrescriptionDate()
        setPrescriptionNotes()
        setPrescriptionMedicine()
    }

    private fun setPrescriptionDate(){
        val startDateTextView: TextView? = view?.findViewById(R.id.prescriptionDateTextView)
        val startHourTextView : TextView? = view?.findViewById(R.id.prescriptionHourTextView)

        setDate(startDateTextView, startHourTextView, prescription?.date)
    }

    private fun setPrescriptionNotes(){
        view?.findViewById<TextView>(R.id.notesTextView)?.text = "${prescription?.notes}"
    }

    private fun setPrescriptionMedicine(){
        view?.findViewById<TextView>(R.id.dosagePrescriptionTextView)?.text = "Brak leków przypisanych do tej recepty"

        val description = StringBuilder()
        val medications = prescription?.medication?.split(";".toRegex()) ?: return
        val dosages = prescription?.dosage?.split(";".toRegex()) ?: return

        for (i in medications.indices){
            if (i != medications.lastIndex) description.append("${medications[i]}\n${dosages[i]}\n\n")
            else description.append("${medications[i]}\n${dosages[i]}")
        }

        view?.findViewById<TextView>(R.id.dosagePrescriptionTextView)?.text = "${description}"
    }
}