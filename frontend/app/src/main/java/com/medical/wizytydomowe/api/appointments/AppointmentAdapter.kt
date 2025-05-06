package com.medical.wizytydomowe.api.appointments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.utils.*

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
            setDateAndTime(appointment)
            setStatus(appointment)

            appointmentView.setOnClickListener {
                onAppointmentClick(appointment)
            }
        }

        private fun setDateAndTime(appointment: Appointment){
            setDate(startDateTextView, startHourTextView, appointment.appointmentStartTime)
        }

        private fun setStatus(appointment: Appointment){
            when (appointment.status) {
                "CANCELED" -> {
                    statusAppointmentTextView.text = "ANULOWANA"
                    statusAppointmentTextView.setTextColor(Color.RED)
                }
                "COMPLETED" -> {
                    statusAppointmentTextView.text = "ODBYTA"
                    statusAppointmentTextView.setTextColor(Color.BLACK)
                }
                "RESERVED" -> {
                    statusAppointmentTextView.text = "ZAREZERWOWANA"
                    statusAppointmentTextView.setTextColor(Color.BLACK)
                }
                "AVAILABLE" -> {
                    statusAppointmentTextView.text = "DOSTĘPNA"
                    statusAppointmentTextView.setTextColor(Color.BLACK)
                }
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

}