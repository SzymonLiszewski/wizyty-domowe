package com.medical.wizytydomowe.fragments.appointments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointmentApi.AppointmentRetrofitInstance
import com.medical.wizytydomowe.api.appointments.AddAppointmentRequest
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.appointments.AppointmentChangeStatusRequest
import com.medical.wizytydomowe.api.appointments.AppointmentRegisterRequest
import com.medical.wizytydomowe.api.authApi.AuthRetrofitInstance
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import com.medical.wizytydomowe.api.utils.*
import com.medical.wizytydomowe.fragments.profile.LoginFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AppointmentDetailsFragment : Fragment(R.layout.appointment_details_fragment) {

    private var appointment: Appointment? = null
    private var addNewAppointmentFlag: String? = null

    private lateinit var preferenceManager : PreferenceManager

    private lateinit var doctorView: MaterialCardView
    private lateinit var nurseView: MaterialCardView
    private lateinit var patientView: MaterialCardView
    private lateinit var addressVerticalView: MaterialCardView
    private lateinit var addressHorizontalView: MaterialCardView
    private lateinit var cancelAppointmentView : MaterialCardView
    private lateinit var finishAppointmentView: MaterialCardView
    private lateinit var registerAppointmentView:MaterialCardView
    private lateinit var goToLoginView: MaterialCardView
    private lateinit var addAppointmentView: MaterialCardView
    private lateinit var noPatientView: MaterialCardView
    private lateinit var noAddressVerticalView: MaterialCardView
    private lateinit var noAddressHorizontalView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointment = arguments?.getSerializable("appointment") as? Appointment
        addNewAppointmentFlag = arguments?.getSerializable("addNewAppointmentFlag") as? String

        cancelAppointmentView = view.findViewById(R.id.cancelAppointmentView)
        finishAppointmentView = view.findViewById(R.id.finishAppointmentView)
        registerAppointmentView = view.findViewById(R.id.registerAppointmentView)
        goToLoginView = view.findViewById(R.id.goToLoginView)
        addAppointmentView = view.findViewById(R.id.addAppointmentView)
        doctorView = view.findViewById(R.id.doctorView)
        nurseView = view.findViewById(R.id.nurseView)
        patientView = view.findViewById(R.id.patientView)
        addressVerticalView = view.findViewById(R.id.addressVerticalView)
        addressHorizontalView = view.findViewById(R.id.addressHorizontalView)
        noPatientView = view.findViewById(R.id.noPatientView)
        noAddressVerticalView = view.findViewById(R.id.noAddressVerticalView)
        noAddressHorizontalView = view.findViewById(R.id.noAddressHorizontalView)

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()
        val token = "Bearer " + preferenceManager.getAuthToken()

        if (userRole == "Nurse") setNurseLayout()
        else if (userRole == "Doctor") setDoctorLayout()
        else setPatientLayout()

        cancelAppointmentView.setOnClickListener { cancelAppointmentDialog() }
        finishAppointmentView.setOnClickListener { finishAppointmentDialog() }
        addAppointmentView.setOnClickListener { addNewAppointmentDialog() }
        registerAppointmentView.setOnClickListener { registerAppointmentDialog() }
        goToLoginView.setOnClickListener { navigateToLoginFragment() }
    }

    private fun navigateToLoginFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(LoginFragment())
    }

    private fun setStatus(){
        val statusTextView = view?.findViewById<TextView>(R.id.statusTextView)

        when (appointment?.status) {
            "CANCELED" -> {
                statusTextView?.text = "ANULOWANA"
                statusTextView?.setTextColor(Color.RED)
            }
            "COMPLETED" -> {
                statusTextView?.text = "ODBYTA"
                statusTextView?.setTextColor(Color.BLACK)
            }
            "RESERVED" -> {
                statusTextView?.text = "ZAREZERWOWANA"
                statusTextView?.setTextColor(Color.BLACK)
            }
            "AVAILABLE" -> {
                statusTextView?.text = "DOSTĘPNA"
                statusTextView?.setTextColor(Color.BLACK)
            }
        }

        if (!appointment?.notes.isNullOrEmpty()) view?.findViewById<TextView>(R.id.notesTextView)?.text = "Dodatkowe informacje:\n" + "${appointment?.notes}"

        if (appointment?.status == "RESERVED" && preferenceManager.getRole() == "Patient") cancelAppointmentView.visibility = View.VISIBLE
        else cancelAppointmentView.visibility = View.GONE

        if (appointment?.status == "RESERVED" && preferenceManager.getRole() != "Patient") finishAppointmentView.visibility = View.VISIBLE
        else finishAppointmentView.visibility = View.GONE

        if (appointment?.status == "AVAILABLE" && preferenceManager.getRole() == "Patient") registerAppointmentView.visibility = View.VISIBLE
        else registerAppointmentView.visibility = View.GONE

        if (appointment?.status == "AVAILABLE" && addNewAppointmentFlag != null) addAppointmentView.visibility = View.VISIBLE
        else addAppointmentView.visibility = View.GONE

        if (appointment?.status == "AVAILABLE" && preferenceManager.getRole() == null) goToLoginView.visibility = View.VISIBLE
        else goToLoginView.visibility = View.GONE
    }

    private fun setAddressData(){
        val cityVerticalTextView = view?.findViewById<TextView>(R.id.cityVerticalTextView)
        val postalCodeVerticalTextView = view?.findViewById<TextView>(R.id.postalCodeVerticalTextView)
        val streetVerticalTextView = view?.findViewById<TextView>(R.id.streetVerticalTextView)
        val cityHorizontalTextView = view?.findViewById<TextView>(R.id.cityHorizontalTextView)
        val postalCodeHorizontalTextView = view?.findViewById<TextView>(R.id.postalCodeHorizontalTextView)
        val streetHorizontalTextView = view?.findViewById<TextView>(R.id.streetHorizontalTextView)

        setAddress(appointment?.address, cityVerticalTextView, postalCodeVerticalTextView, streetVerticalTextView)
        setAddress(appointment?.address, cityHorizontalTextView, postalCodeHorizontalTextView, streetHorizontalTextView)
    }

    private fun setEndData(){
        val endDateTextView: TextView? = view?.findViewById(R.id.endDateTextView)
        val endHourTextView : TextView? = view?.findViewById(R.id.endHourTextView)

        setDate(endDateTextView, endHourTextView, appointment?.appointmentEndTime)
    }

    private fun setStartData(){
        val startDateTextView: TextView? = view?.findViewById(R.id.startDateTextView)
        val startHourTextView : TextView? = view?.findViewById(R.id.startHourTextView)

        setDate(startDateTextView, startHourTextView, appointment?.appointmentStartTime)
    }

    private fun setPatientData(){
        view?.findViewById<TextView>(R.id.firstNamePatientTextView)?.text = "${appointment?.patient?.firstName}"
        view?.findViewById<TextView>(R.id.lastNamePatientTextView)?.text = "${appointment?.patient?.lastName}"
        val phoneNumberConverted = "${appointment?.patient?.phoneNumber?.substring(0,3)}-${appointment?.patient?.phoneNumber?.substring(3,6)}-${appointment?.patient?.phoneNumber?.substring(6)}"
        view?.findViewById<TextView>(R.id.phoneNumberPatientTextView)?.text = "${phoneNumberConverted}"
        view?.findViewById<TextView>(R.id.emailPatientTextView)?.text = "${appointment?.patient?.email}"
    }

    private fun setNurseData(){
        view?.findViewById<TextView>(R.id.firstNameNurseTextView)?.text = "${appointment?.nurse?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameNurseTextView)?.text = "${appointment?.nurse?.lastName}"
        view?.findViewById<TextView>(R.id.specializationNurseTextView)?.text = "Pielęgniarka"
        view?.findViewById<TextView>(R.id.hospitalNurseTextView)?.text = "${appointment?.nurse?.workPlace?.name}"
    }

    private fun setDoctorData(){
        view?.findViewById<TextView>(R.id.firstNameDoctorTextView)?.text = "${appointment?.doctor?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameDoctorTextView)?.text = "${appointment?.doctor?.lastName}"
        view?.findViewById<TextView>(R.id.specializationDoctorTextView)?.text = "${appointment?.doctor?.specialization}"
        view?.findViewById<TextView>(R.id.hospitalDoctorTextView)?.text = "${appointment?.doctor?.workPlace?.name}"
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
    }

    private fun setAddressHorizontalLayoutAndView(){
        if (appointment?.address != null){
            addressHorizontalView.visibility = View.VISIBLE
            noAddressHorizontalView.visibility = View.GONE
            setAddressData()
        }
        else {
            addressHorizontalView.visibility = View.GONE
            noAddressHorizontalView.visibility = View.VISIBLE
        }
    }

    private fun setPatientLayoutWithDoctorAndNurse(){
        doctorView.visibility = View.VISIBLE
        nurseView.visibility = View.VISIBLE
        patientView.visibility = View.GONE
        noPatientView.visibility - View.GONE
        addressVerticalView.visibility = View.GONE
        noAddressVerticalView.visibility = View.GONE

        setAddressHorizontalLayoutAndView()

        setLayoutPositionInGrid(doctorView, 0, 0)
        setLayoutPositionInGrid(nurseView, 0, 1)
    }

    private fun setPatientViewWithDoctor(){
        setDoctorData()
        setStatus()
        setStartData()
        setEndData()
    }

    private fun setAddressVerticaLayoutAndView(){
        if (appointment?.address != null){
            noAddressVerticalView.visibility = View.GONE
            addressVerticalView.visibility = View.VISIBLE
            setLayoutPositionInGrid(addressVerticalView, 0, 1)
            setAddressData()
        }
        else{
            noAddressVerticalView.visibility = View.VISIBLE
            addressVerticalView.visibility = View.GONE
        }
    }

    private fun setPatientLayoutWithDoctor(){
        doctorView.visibility = View.VISIBLE
        nurseView.visibility = View.GONE
        patientView.visibility = View.GONE
        noPatientView.visibility - View.GONE
        addressHorizontalView.visibility = View.GONE
        noAddressHorizontalView.visibility = View.GONE

        setAddressVerticaLayoutAndView()

        setLayoutPositionInGrid(doctorView, 0, 0)
    }

    private fun setPatientViewWithNurse(){
        setNurseData()
        setStatus()
        setStartData()
        setEndData()
    }

    private fun setPatientLayoutWithNurse(){
        doctorView.visibility = View.GONE
        nurseView.visibility = View.VISIBLE
        patientView.visibility = View.GONE
        noPatientView.visibility - View.GONE
        addressHorizontalView.visibility = View.GONE
        noAddressHorizontalView.visibility = View.GONE

        setAddressVerticaLayoutAndView()

        setLayoutPositionInGrid(nurseView, 0, 0)
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
        setStatus()
        setStartData()
        setEndData()
    }

    private fun setPatientLayoutAndViewForMedicalStaff(){
        if (appointment?.patient != null){
            patientView.visibility = View.VISIBLE
            noPatientView.visibility = View.GONE
            setLayoutPositionInGrid(patientView, 0, 0)
            setPatientData()
        }
        else {
            patientView.visibility = View.GONE
            noPatientView.visibility = View.VISIBLE
        }
    }

    private fun setDoctorLayoutWithNurse(){
        doctorView.visibility = View.GONE
        nurseView.visibility = View.VISIBLE
        addressVerticalView.visibility = View.GONE
        noAddressVerticalView.visibility = View.GONE

        setAddressHorizontalLayoutAndView()
        setPatientLayoutAndViewForMedicalStaff()

        setLayoutPositionInGrid(nurseView, 0, 1)
    }

    private fun setDoctorViewWithoutNurse(){
        setStatus()
        setStartData()
        setEndData()
    }

    private fun setDoctorLayoutWithoutNurse(){
        doctorView.visibility = View.GONE
        nurseView.visibility = View.GONE
        addressHorizontalView.visibility = View.GONE
        noAddressHorizontalView.visibility = View.GONE

        setAddressVerticaLayoutAndView()
        setPatientLayoutAndViewForMedicalStaff()
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
        setDoctorData()
        setStatus()
        setStartData()
        setEndData()
    }

    private fun setNurseLayoutWithDoctor(){
        doctorView.visibility = View.VISIBLE
        nurseView.visibility = View.GONE
        addressVerticalView.visibility = View.GONE
        noAddressVerticalView.visibility = View.GONE

        setAddressHorizontalLayoutAndView()
        setPatientLayoutAndViewForMedicalStaff()

        setLayoutPositionInGrid(doctorView, 0, 1)
    }

    private fun setNurseViewWithoutDoctor(){
        setStatus()
        setStartData()
        setEndData()
    }

    private fun setNurseLayoutWithoutDoctor(){
        doctorView.visibility = View.GONE
        nurseView.visibility = View.GONE
        addressHorizontalView.visibility = View.GONE
        noAddressHorizontalView.visibility = View.GONE

        setAddressVerticaLayoutAndView()

        setPatientLayoutAndViewForMedicalStaff()

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

    private fun navigateToAppointmentFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AppointmentsFragment())
    }

    private fun addNewAppointment(){
        val addAppointmentRequest = AddAppointmentRequest(appointment?.status, appointment?.appointmentStartTime,
            appointment?.appointmentEndTime, appointment?.doctor?.id, appointment?.nurse?.id,
            appointment?.patient?.id, appointment?.address, appointment?.notes)

        AppointmentRetrofitInstance.appointmentApiService.addSingleAppointment(addAppointmentRequest)
            .enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Wizyta została dodana pomyślnie.", Toast.LENGTH_LONG).show()
                    navigateToAppointmentFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Podczas tworzenia wizyty wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addNewAppointmentDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz dodać tę wizytę?"){
            addNewAppointment()
        }
    }

    private fun cancelAppointment(){
        val token = "Bearer " + preferenceManager.getAuthToken()

        AppointmentRetrofitInstance.appointmentApiService.cancelAppointment(appointment?.id.toString(), token)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Wizyta została anulowana pomyślnie.", Toast.LENGTH_LONG).show()
                        navigateToAppointmentFragment()
                    }
                    else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(context, "Podczas anulowania wizyty wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cancelAppointmentDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz anulować wizytę?"){
            cancelAppointment()
        }
    }

    private fun finishAppointment(){
        val token = "Bearer " + preferenceManager.getAuthToken()
        val appointmentChangeStatusRequest = AppointmentChangeStatusRequest("COMPLETED")

        AppointmentRetrofitInstance.appointmentApiService.finishAppointment(token, appointment?.id.toString(),
            appointmentChangeStatusRequest)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Status wizyty został pomyślnie zmieniony.", Toast.LENGTH_LONG).show()
                        navigateToAppointmentFragment()
                    }
                    else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(context, "Podczas zmiany statusu wizyty wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun finishAppointmentDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz uznać wizytę za odbytą?"){
            finishAppointment()
        }
    }

    private fun registerAppointment(){
        val token = "Bearer " + preferenceManager.getAuthToken()
        val appointmentRegisterRequest = AppointmentRegisterRequest(appointment?.id, appointment?.address)

        AppointmentRetrofitInstance.appointmentApiService.registerPatientOnAppointment(token, appointmentRegisterRequest)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Wizyta została zarezerwowana pomyślnie.", Toast.LENGTH_LONG).show()
                        navigateToAppointmentFragment()
                    }
                    else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(context, "Podczas rezerwacji wizyty wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun registerAppointmentDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz zarezerwować tę wizytę?"){
            registerAppointment()
        }
    }
}