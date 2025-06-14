package com.medical.wizytydomowe.api.prescriptions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.utils.*


class PrescriptionAdapter(
    private var prescriptions: List<Prescription>,
    private val onPrescriptionClick: (Prescription) -> Unit
) : RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {

    inner class PrescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val prescriptionDateTextView: TextView = itemView.findViewById(R.id.prescriptionDateTextView)
        private val prescriptionHourTextView: TextView = itemView.findViewById(R.id.prescriptionHourTextView)
        private val prescriptionRoleTextView: TextView = itemView.findViewById(R.id.prescriptionRoleTextView)
        private val prescriptionFirstNameTextView: TextView = itemView.findViewById(R.id.prescriptionFirstNameTextView)
        private val prescriptionLastNameTextView: TextView = itemView.findViewById(R.id.prescriptionLastNameTextView)
        private val prescriptionView: MaterialCardView = itemView.findViewById(R.id.prescriptionView)

        fun bind(prescription: Prescription) {
            val preferenceManager = PreferenceManager(itemView.context)

            if (preferenceManager.getRole() == "Patient"){
                setDoctorData(prescription)
            }
            else {
                setPatientData(prescription)
            }

            setPrescriptionDate(prescription)

            prescriptionView.setOnClickListener {
                onPrescriptionClick(prescription)
            }
        }

        private fun setPatientData(prescription: Prescription){
            prescriptionRoleTextView.text = "Pacjent:"
            prescriptionFirstNameTextView.text = "${prescription.patient?.firstName}"
            prescriptionLastNameTextView.text = "${prescription.patient?.lastName}"
        }

        private fun setDoctorData(prescription: Prescription){
            prescriptionRoleTextView.text = "Lekarz:"
            prescriptionFirstNameTextView.text = "${prescription.doctor?.firstName}"
            prescriptionLastNameTextView.text = "${prescription.doctor?.lastName}"
        }

        private fun setPrescriptionDate(prescription: Prescription){
            setDate(prescriptionDateTextView, prescriptionHourTextView, prescription.prescriptionTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prescription_item, parent, false)
        return PrescriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        holder.bind(prescriptions[position])
    }

    override fun getItemCount(): Int = prescriptions.size

}