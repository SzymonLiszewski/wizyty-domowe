package com.medical.wizytydomowe.api.appointments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.R


class AppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onAppointmentDetailsClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStartTime: TextView = itemView.findViewById(R.id.tvAppointmentStartTime)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnAppointmentDetails: Button = itemView.findViewById(R.id.btnAppointmentDetails)

        fun bind(appointment: Appointment) {
            tvStartTime.text = "${appointment.appointmentStartTime}"

            when (appointment.status) {
                "cancelled" -> tvStatus.text = "Anulowana"
                "completed" -> tvStatus.text = "Odbyta"
                "reserved" -> tvStatus.text = "Zarezerwowana"
                "available" -> tvStatus.text = "Dostępna"
            }

            when (tvStatus.text) {
                "Anulowana" -> tvStatus.setTextColor(Color.RED)
                "Odbyta" -> tvStatus.setTextColor(Color.BLACK)
                "Zarezerwowana" -> tvStatus.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.gray)
                )
                else -> tvStatus.setTextColor(Color.BLACK)
            }

            btnAppointmentDetails.setOnClickListener {
                onAppointmentDetailsClick(appointment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.visit_item, parent, false)
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
}