package com.medical.wizytydomowe.api.appointments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.R
import java.text.SimpleDateFormat
import java.util.Locale


class AppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onAppointmentClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startDateTextView: TextView = itemView.findViewById(R.id.startDateTextView)
        private val startHourTextView: TextView = itemView.findViewById(R.id.startHourTextView)
        private val statusAppointmentTextView: TextView = itemView.findViewById(R.id.statusAppointmentTextView)
        private val appointmentView: MaterialCardView = itemView.findViewById(R.id.appointmentView)

        fun bind(appointment: Appointment) {
            val parts = appointment.appointmentStartTime?.split("T".toRegex(), 2)
            if (parts?.size == 2) {
                val date = convertToDateFormat(parts[0].trim())
                val time = parts[1].trim()
                if (!date.isNullOrEmpty()) startDateTextView.text = "${date}"
                else startDateTextView.text = "None"
                startHourTextView.text = "${time}"
            }
            else{
                startHourTextView.text = "None"
                startDateTextView.text = "None"
            }

            when (appointment.status) {
                "CANCELED" -> {
                    statusAppointmentTextView.text = "Anulowana"
                    statusAppointmentTextView.setTextColor(Color.RED)
                }
                "COMPLETED" -> {
                    statusAppointmentTextView.text = "Odbyta"
                    statusAppointmentTextView.setTextColor(Color.BLACK)
                }
                "RESERVED" -> {
                    statusAppointmentTextView.text = "Zarezerwowana"
                    statusAppointmentTextView.setTextColor(Color.BLACK)
                }
                "AVAILABLE" -> {
                    statusAppointmentTextView.text = "Dostępna"
                    statusAppointmentTextView.setTextColor(Color.BLACK)
                }
            }

            appointmentView.setOnClickListener {
                onAppointmentClick(appointment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.appointment_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size

    fun updateAppointments(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }

    private fun convertToDateFormat(dateString: String?): String? {
        try {
            if (!dateString.isNullOrEmpty()){
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                return date?.let { outputFormat.format(it) }
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }
}