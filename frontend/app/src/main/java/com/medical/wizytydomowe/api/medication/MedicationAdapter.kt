package com.medical.wizytydomowe.api.medication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.users.User

class MedicationAdapter(
    private var medications: List<Medication>,
    private val onMedicationClick: (Medication) -> Unit
) : RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    inner class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicationNameTextView: TextView =
            itemView.findViewById(R.id.medicationNameTextView)
        private val removeMedicationButton: ImageView =
            itemView.findViewById(R.id.removeMedicationButton)

        fun bind(medication: Medication) {
            medicationNameTextView.text = medication.name

            removeMedicationButton.setOnClickListener {
                onMedicationClick(medication)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.medication_item, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        holder.bind(medications[position])
    }

    override fun getItemCount(): Int = medications.size

    fun updateMedications(newMedications: List<Medication>) {
        medications = newMedications
        notifyDataSetChanged()
    }
}