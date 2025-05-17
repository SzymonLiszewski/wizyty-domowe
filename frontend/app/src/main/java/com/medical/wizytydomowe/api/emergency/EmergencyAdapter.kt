package com.medical.wizytydomowe.api.emergency

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.utils.*

class EmergencyAdapter(
    private var emergencies: List<Emergency>,
    private val onEmergencyClick: (Emergency) -> Unit
) : RecyclerView.Adapter<EmergencyAdapter.EmergencyViewHolder>() {

    inner class EmergencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emergencyDateTextView: TextView = itemView.findViewById(R.id.emergencyDateTextView)
        private val emergencyHourTextView: TextView = itemView.findViewById(R.id.emergencyHourTextView)
        private val statusEmergencyTextView: TextView = itemView.findViewById(R.id.statusEmergencyTextView)
        private val emergencyView: MaterialCardView = itemView.findViewById(R.id.emergencyView)

        fun bind(emergency: Emergency) {
            setEmergencyStatus(emergency)
            setEmergencyDate(emergency)

            emergencyView.setOnClickListener {
                onEmergencyClick(emergency)
            }
        }

        private fun setEmergencyStatus(emergency: Emergency){
            when (emergency.status) {
                "AVAILABLE" -> {
                    statusEmergencyTextView.text = "DOSTĘPNE"
                    statusEmergencyTextView.setTextColor(Color.rgb(80,200,120))
                }
                "IN PROGRESS" -> {
                    statusEmergencyTextView.text = "W TOKU"
                    statusEmergencyTextView.setTextColor(Color.RED)
                }
                "COMPLETED" -> {
                    statusEmergencyTextView.text = "ZAKOŃCZONE"
                    statusEmergencyTextView.setTextColor(Color.BLACK)
                }
            }
        }

        private fun setEmergencyDate(emergency: Emergency){
            setDate(emergencyDateTextView, emergencyHourTextView, emergency.date)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emergency_item, parent, false)
        return EmergencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmergencyViewHolder, position: Int) {
        holder.bind(emergencies[position])
    }

    override fun getItemCount(): Int = emergencies.size

}