package com.medical.wizytydomowe.api.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.R

class UserAdapter(
    private var users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userView: MaterialCardView = itemView.findViewById(R.id.userView)
        private val firstNameUserTextView: TextView = itemView.findViewById(R.id.firstNameUserTextView)
        private val lastNameUserTextView: TextView = itemView.findViewById(R.id.lastNameUserTextView)
        private val specializationMedicalStaffTextView: TextView = itemView.findViewById(R.id.specializationMedicalStaffTextView)
        private val hospitalMedicalStaffTextView: TextView = itemView.findViewById(R.id.hospitalMedicalStaffTextView)
        private val phoneNumberPatientTextView: TextView = itemView.findViewById(R.id.phoneNumberPatientTextView)
        private val emailPatientTextView: TextView = itemView.findViewById(R.id.emailPatientTextView)
        private val patientImage: ImageView = itemView.findViewById(R.id.patientImage)
        private val doctorImage: ImageView = itemView.findViewById(R.id.doctorImage)
        private val nurseImage: ImageView = itemView.findViewById(R.id.nurseImage)

        fun bind(user: User) {
            setImage(user)
            setLayout(user)
            setData(user)

            userView.setOnClickListener {
                onUserClick(user)
            }
        }

        fun setImage(user: User){
            if (user is Patient) setPatientImage()
            else if (user is Doctor) setDoctorImage()
            else if (user is Nurse) setNurseImage()
        }

        fun setNurseImage(){
            patientImage.visibility = View.GONE
            doctorImage.visibility = View.GONE
            nurseImage.visibility = View.VISIBLE
        }

        fun setDoctorImage(){
            patientImage.visibility = View.GONE
            doctorImage.visibility = View.VISIBLE
            nurseImage.visibility = View.GONE
        }

        fun setPatientImage(){
            patientImage.visibility = View.VISIBLE
            doctorImage.visibility = View.GONE
            nurseImage.visibility = View.GONE
        }

        fun setLayout(user: User){
            if (user is Patient) setPatientLayout()
            else setMedicalstaffLayout()
        }

        fun setMedicalstaffLayout(){
            phoneNumberPatientTextView.visibility = View.GONE
            emailPatientTextView.visibility = View.GONE
            hospitalMedicalStaffTextView.visibility = View.VISIBLE
            specializationMedicalStaffTextView.visibility = View.VISIBLE
        }

        fun setPatientLayout(){
            phoneNumberPatientTextView.visibility = View.VISIBLE
            emailPatientTextView.visibility = View.VISIBLE
            hospitalMedicalStaffTextView.visibility = View.GONE
            specializationMedicalStaffTextView.visibility = View.GONE
        }

        fun setData(user: User){
            setPersonalData(user)
            if (user is Patient) setPatientData(user)
            else if (user is Doctor) setDoctorData(user)
            else if (user is Nurse) setNurseData(user)
        }

        fun setNurseData(nurse: Nurse){
            specializationMedicalStaffTextView.text = "Pielęgniarka"
            hospitalMedicalStaffTextView.text = nurse.workPlace
        }

        fun setDoctorData(doctor: Doctor){
            specializationMedicalStaffTextView.text = doctor.specialization
            hospitalMedicalStaffTextView.text = doctor.workPlace
        }

        fun setPatientData(patient: Patient){
            emailPatientTextView.text = patient.email
            phoneNumberPatientTextView.text = "123-456-789"
        }

        fun setPersonalData(user: User){
            firstNameUserTextView.text = user.firstName
            lastNameUserTextView.text = user.lastName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}