package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.prescriptions.Prescription

class PrescriptionDetailsFragment : Fragment(R.layout.prescription_details) {

    private var prescription: Prescription? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prescription = arguments?.getSerializable("prescription") as? Prescription

        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvPatient: TextView = view.findViewById(R.id.tvPatient)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvDoctorName : TextView = view.findViewById(R.id.tvDoctorName)
        val tvDoctorSurname : TextView = view.findViewById(R.id.tvDoctorSurname)
        val tvDoctorSpeciality : TextView = view.findViewById(R.id.tvDoctorSpeciality)

        tvDate.text = "Data wystawienia: ${prescription?.prescriptionTime}";
        tvPatient.text = "Pacjent: ${prescription?.patient?.firstName} ${prescription?.patient?.lastName}";
        tvDoctorName.text = "Imię: ${prescription?.doctor?.firstName}";
        tvDoctorSurname.text = "Nazwisko: ${prescription?.doctor?.lastName}";
        tvDoctorSpeciality.text = "Specjalność: ${prescription?.doctor?.specialization}";
        tvDescription.text = "${prescription?.notes}"

    }


}