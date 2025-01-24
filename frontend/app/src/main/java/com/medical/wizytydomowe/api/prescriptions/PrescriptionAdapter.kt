package com.medical.wizytydomowe.api.prescriptions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.R


class PrescriptionAdapter(
    private var prescriptions: List<Prescription>,
    private val onPrescriptionDetailsClick: (Prescription) -> Unit
) : RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {

    inner class PrescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPrescriptionTime: TextView = itemView.findViewById(R.id.tvPrescriptionTime)
        private val tvPrescriptionDoctor: TextView = itemView.findViewById(R.id.tvPrescriptionDoctor)
        private val btnPrescriptionDetails: Button = itemView.findViewById(R.id.btnPrescriptionDetails)

        fun bind(prescription: Prescription) {
            tvPrescriptionTime.text = "${prescription.prescriptionTime}"
            tvPrescriptionDoctor.text = "Wystawił: ${prescription.doctor?.firstName} ${prescription.doctor?.lastName}"

            btnPrescriptionDetails.setOnClickListener {
                onPrescriptionDetailsClick(prescription)
            }
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