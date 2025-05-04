package com.medical.wizytydomowe.fragments.appointments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.fragments.profile.LoginFragment
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentDetails : Fragment(R.layout.appointment_details_fragment) {

    private var appointment: Appointment? = null

    private lateinit var cancelAppointmentButton : Button

    private lateinit var preferenceManager : PreferenceManager

    private lateinit var doctorView: MaterialCardView
    private lateinit var nurseView: MaterialCardView
    private lateinit var patientView: MaterialCardView
    private lateinit var addressVerticalView: MaterialCardView
    private lateinit var addressHorizontalView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointment = arguments?.getSerializable("appointment") as? Appointment

        cancelAppointmentButton = view.findViewById(R.id.cancelAppointmentButton)
        doctorView = view.findViewById(R.id.doctorView)
        nurseView = view.findViewById(R.id.nurseView)
        patientView = view.findViewById(R.id.patientView)
        addressVerticalView = view.findViewById(R.id.addressVerticalView)
        addressHorizontalView = view.findViewById(R.id.addressHorizontalView)

        preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()
        val userRole = preferenceManager.getRole()

        if (userToken != null) {
            if (userRole == "Patient") setPatientLayout()
            else if (userRole == "Doctor") setDoctorLayout()
            else setNurseLayout()
        }
        else{
            moveToLoginFragment()
        }

        cancelAppointmentButton.setOnClickListener {
            showCancelAppointmentDialog()
        }

    }

    private fun showCancelAppointmentDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Uwaga!")
            .setIcon(R.drawable.status_information_svgrepo_com)
            .setMessage("Czy na pewno chcesz anulować wizytę?")
            .setPositiveButton("Tak") { dialog, which ->
                //TODO send request to the backend and toast
            }
            .setNegativeButton("Nie") { dialog, which ->
                dialog.dismiss()
            }
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
    }

    private fun setStatus(){
        val statusTextView = view?.findViewById<TextView>(R.id.statusTextView)

        when (appointment?.status) {
            "CANCELED" -> {
                statusTextView?.text = "Anulowana"
                statusTextView?.setTextColor(Color.RED)
            }
            "COMPLETED" -> {
                statusTextView?.text = "Odbyta"
                statusTextView?.setTextColor(Color.BLACK)
            }
            "RESERVED" -> {
                statusTextView?.text = "Zarezerwowana"
                statusTextView?.setTextColor(Color.BLACK)
            }
            "AVAILABLE" -> {
                statusTextView?.text = "Dostępna"
                statusTextView?.setTextColor(Color.BLACK)
            }
        }

        if (!appointment?.notes.isNullOrEmpty()){
            view?.findViewById<TextView>(R.id.notesTextView)?.text = "Dodatkowe informacje:\n" + "${appointment?.notes}"
        }

        cancelAppointmentButton.visibility = if (statusTextView?.text == "Zarezerwowana" && preferenceManager?.getRole() == "Patient") {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun splitAddress(address: String?) : List<String>{
        val splitAddress = mutableListOf<String>()
        val parts = address?.split(",".toRegex(), 3)
        if (parts?.size == 3) {
            splitAddress.add(parts[0].trim())
            splitAddress.add(parts[1].trim())
            splitAddress.add("ul. " + parts[2].trim())
        }
        return splitAddress
    }

    private fun setAddressAsUnknown(){
        view?.findViewById<TextView>(R.id.cityVerticalTextView)?.text = "None"
        view?.findViewById<TextView>(R.id.postalCodeVerticalTextView)?.text = "None"
        view?.findViewById<TextView>(R.id.streetVerticalTextView)?.text = "None"
        view?.findViewById<TextView>(R.id.cityHorizontalTextView)?.text = "None"
        view?.findViewById<TextView>(R.id.postalCodeHorizontalTextView)?.text = "None"
        view?.findViewById<TextView>(R.id.streetHorizontalTextView)?.text = "None"
    }

    private fun setAddressData(){
        if (!appointment?.address.isNullOrEmpty()){
            val parts = splitAddress(appointment?.address)
            if (parts.size == 3){
                view?.findViewById<TextView>(R.id.cityVerticalTextView)?.text = parts[0]
                view?.findViewById<TextView>(R.id.postalCodeVerticalTextView)?.text = parts[1]
                view?.findViewById<TextView>(R.id.streetVerticalTextView)?.text = parts[2]
                view?.findViewById<TextView>(R.id.cityHorizontalTextView)?.text = parts[0]
                view?.findViewById<TextView>(R.id.postalCodeHorizontalTextView)?.text = parts[1]
                view?.findViewById<TextView>(R.id.streetHorizontalTextView)?.text = parts[2]
            }
            else setAddressAsUnknown()
        }
        else setAddressAsUnknown()
    }

    private fun setDataAsUnknown(dateTextView: TextView?, hourTextView : TextView?){
        hourTextView?.text = "None"
        dateTextView?.text = "None"
    }

    private fun splitData(dateTextView: TextView?, hourTextView : TextView?, dateString: String?){
        val parts = dateString?.split("T".toRegex(), 2)
        if (parts?.size == 2) {
            val date = convertToDateFormat(parts[0].trim())
            val time = parts[1].trim()
            if (!date.isNullOrEmpty()) {
                dateTextView?.text = "${date}"
                hourTextView?.text = "${time}"
            }
            else setDataAsUnknown(dateTextView, hourTextView)

        }
        else setDataAsUnknown(dateTextView, hourTextView)
    }

    private fun setEndData(){
        val endDateTextView: TextView? = view?.findViewById(R.id.endDateTextView)
        val endHourTextView : TextView? = view?.findViewById(R.id.endHourTextView)

        splitData(endDateTextView, endHourTextView, appointment?.appointmentEndTime)
    }


    private fun setStartData(){
        val startDateTextView: TextView? = view?.findViewById(R.id.startDateTextView)
        val startHourTextView : TextView? = view?.findViewById(R.id.startHourTextView)

        splitData(startDateTextView, startHourTextView, appointment?.appointmentStartTime)
    }

    private fun setPatientData(){
        view?.findViewById<TextView>(R.id.firstNamePatientTextView)?.text = "${appointment?.patient?.firstName}"
        view?.findViewById<TextView>(R.id.lastNamePatientTextView)?.text = "${appointment?.patient?.lastName}"
        view?.findViewById<TextView>(R.id.phoneNumberPatientTextView)?.text = "123-456-789"
        view?.findViewById<TextView>(R.id.emailPatientTextView)?.text = "janrogowski@gmail.com"
    }

    private fun setNurseData(){
        view?.findViewById<TextView>(R.id.firstNameNurseTextView)?.text = "${appointment?.nurse?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameNurseTextView)?.text = "${appointment?.nurse?.lastName}"
        view?.findViewById<TextView>(R.id.specializationNurseTextView)?.text = "Pielęgniarka"
        view?.findViewById<TextView>(R.id.hospitalNurseTextView)?.text = "${appointment?.nurse?.workPlace}"
    }

    private fun setDoctorData(){
        view?.findViewById<TextView>(R.id.firstNameDoctorTextView)?.text = "${appointment?.doctor?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameDoctorTextView)?.text = "${appointment?.doctor?.lastName}"
        view?.findViewById<TextView>(R.id.specializationDoctorTextView)?.text = "${appointment?.doctor?.specialization}"
        view?.findViewById<TextView>(R.id.hospitalDoctorTextView)?.text = "${appointment?.doctor?.workPlace}"
    }

    private fun setLayoutPositionInGrid(view: MaterialCardView, row: Int, column: Int){
        val params = view.layoutParams as GridLayout.LayoutParams

        params.rowSpec = GridLayout.spec(row)
        params.columnSpec = GridLayout.spec(column)

        params.setGravity(Gravity.CENTER)

        view.layoutParams = params
    }

    private fun setPatientViewWithDoctorAndNurse(){
        setDoctorData()
        setNurseData()
        setStatus()
        setStartData()
        setEndData()
        setAddressData()
    }

    private fun setPatientLayoutWithDoctorAndNurse(){
        doctorView.visibility = View.VISIBLE
        nurseView.visibility = View.VISIBLE
        patientView.visibility = View.GONE
        addressVerticalView.visibility = View.GONE
        addressHorizontalView.visibility = View.VISIBLE

        setLayoutPositionInGrid(doctorView, 0, 0)
        setLayoutPositionInGrid(nurseView, 0, 1)
    }

    private fun setPatientViewWithDoctor(){
        setDoctorData()
        setStatus()
        setStartData()
        setEndData()
        setAddressData()
    }

    private fun setPatientLayoutWithDoctor(){
        doctorView.visibility = View.VISIBLE
        nurseView.visibility = View.GONE
        patientView.visibility = View.GONE
        addressVerticalView.visibility = View.VISIBLE
        addressHorizontalView.visibility = View.GONE

        setLayoutPositionInGrid(doctorView, 0, 0)
        setLayoutPositionInGrid(addressVerticalView, 0, 1)
    }

    private fun setPatientViewWithNurse(){
        setNurseData()
        setStatus()
        setStartData()
        setEndData()
        setAddressData()
    }

    private fun setPatientLayoutWithNurse(){
        doctorView.visibility = View.GONE
        nurseView.visibility = View.VISIBLE
        patientView.visibility = View.GONE
        addressVerticalView.visibility = View.VISIBLE
        addressHorizontalView.visibility = View.GONE

        setLayoutPositionInGrid(nurseView, 0, 0)
        setLayoutPositionInGrid(addressVerticalView, 0, 1)
    }

    private fun setPatientLayout(){
        if (appointment?.nurse != null && appointment?.doctor != null){
            setPatientViewWithDoctorAndNurse()
            setPatientLayoutWithDoctorAndNurse()
        }
        else if (appointment?.nurse == null){
            setPatientViewWithDoctor()
            setPatientLayoutWithDoctor()
        }
        else{
            setPatientViewWithNurse()
            setPatientLayoutWithNurse()
        }
    }

    private fun setDoctorViewWithNurse(){
        setNurseData()
        setPatientData()
        setStatus()
        setStartData()
        setEndData()
        setAddressData()
    }

    private fun setDoctorLayoutWithNurse(){
        doctorView.visibility = View.GONE
        nurseView.visibility = View.VISIBLE
        patientView.visibility = View.VISIBLE
        addressVerticalView.visibility = View.GONE
        addressHorizontalView.visibility = View.VISIBLE

        setLayoutPositionInGrid(patientView, 0, 0)
        setLayoutPositionInGrid(nurseView, 0, 1)
    }

    private fun setDoctorViewWithoutNurse(){
        setPatientData()
        setStatus()
        setStartData()
        setEndData()
        setAddressData()
    }

    private fun setDoctorLayoutWithoutNurse(){
        doctorView.visibility = View.GONE
        nurseView.visibility = View.GONE
        patientView.visibility = View.VISIBLE
        addressVerticalView.visibility = View.VISIBLE
        addressHorizontalView.visibility = View.GONE

        setLayoutPositionInGrid(patientView, 0, 0)
        setLayoutPositionInGrid(addressVerticalView, 0, 1)
    }

    private fun setDoctorLayout(){
        if (appointment?.nurse != null){
            setDoctorViewWithNurse()
            setDoctorLayoutWithNurse()
        }
        else{
            setDoctorViewWithoutNurse()
            setDoctorLayoutWithoutNurse()
        }
    }

    private fun setNurseViewWithDoctor(){
        setPatientData()
        setDoctorData()
        setStatus()
        setStartData()
        setEndData()
        setAddressData()
    }

    private fun setNurseLayoutWithDoctor(){
        doctorView.visibility = View.VISIBLE
        nurseView.visibility = View.GONE
        patientView.visibility = View.VISIBLE
        addressVerticalView.visibility = View.GONE
        addressHorizontalView.visibility = View.VISIBLE

        setLayoutPositionInGrid(patientView, 0, 0)
        setLayoutPositionInGrid(doctorView, 0, 1)
    }

    private fun setNurseViewWithoutDoctor(){
        setPatientData()
        setStatus()
        setStartData()
        setEndData()
        setAddressData()
    }

    private fun setNurseLayoutWithoutDoctor(){
        doctorView.visibility = View.GONE
        nurseView.visibility = View.GONE
        patientView.visibility = View.VISIBLE
        addressVerticalView.visibility = View.VISIBLE
        addressHorizontalView.visibility = View.GONE

        setLayoutPositionInGrid(patientView, 0, 0)
        setLayoutPositionInGrid(addressVerticalView, 0, 1)
    }

    private fun setNurseLayout(){
        if (appointment?.doctor != null){
            setNurseViewWithDoctor()
            setNurseLayoutWithDoctor()
        }
        else{
            setNurseViewWithoutDoctor()
            setNurseLayoutWithoutDoctor()
        }
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

    private fun moveToLoginFragment(){
        val loginFragment = LoginFragment()

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(loginFragment)
    }

}