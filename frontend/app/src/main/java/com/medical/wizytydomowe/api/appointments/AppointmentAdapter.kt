package com.medical.wizytydomowe.api.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.R


class AppointmentAdapter(
    private val appointments: List<Appointment>,
    private val onAppointmentClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStartTime: TextView = itemView.findViewById(R.id.tvAppointmentStartTime)
        private val tvEndTime: TextView = itemView.findViewById(R.id.tvAppointmentEndTime)
        private val tvDoctorId: TextView = itemView.findViewById(R.id.tvDoctorId)
        private val btnMakeAppointment: Button = itemView.findViewById(R.id.btnMakeAppointment)

        fun bind(appointment: Appointment) {
            tvStartTime.text = "Start: ${appointment.appointmentStartTime}"
            tvEndTime.text = "End: ${appointment.appointmentEndTime}"
            if (appointment.doctor != null) {
                tvDoctorId.text = "Doctor ID: ${appointment.doctor.id}"
            }
            else{
                tvDoctorId.text = "Doctor is not assigned."
            }

            btnMakeAppointment.setOnClickListener {
                onAppointmentClick(appointment)
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
}