package com.medical.wizytydomowe.api.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.R

class DoctorAdapter(
    private var doctors: List<Doctor>,
    private val onAvailableDatesClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDoctorPersonalData: TextView = itemView.findViewById(R.id.tvDoctorPersonalData)
        private val tvSpecialization: TextView = itemView.findViewById(R.id.tvSpecialization)
        private val tvWorkPlace: TextView = itemView.findViewById(R.id.tvWorkPlace)
        private val btnAvailableDates: Button = itemView.findViewById(R.id.btnAvailableDates)

        fun bind(doctor: Doctor) {
            tvDoctorPersonalData.text = "${doctor.firstName} ${doctor.lastName}"
            tvSpecialization.text = "${doctor.specialization}"
            tvWorkPlace.text = "${doctor.workPlace}"

            btnAvailableDates.setOnClickListener {
                onAvailableDatesClick(doctor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_item, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(doctors[position])
    }

    override fun getItemCount(): Int = doctors.size

    fun updateDoctors(newDoctors: List<Doctor>) {
        doctors = newDoctors
        notifyDataSetChanged()
    }
}