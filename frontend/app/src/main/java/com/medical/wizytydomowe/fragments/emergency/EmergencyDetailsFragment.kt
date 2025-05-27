package com.medical.wizytydomowe.fragments.emergency

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointmentApi.AppointmentRetrofitInstance
import com.medical.wizytydomowe.api.emergency.EmergencyChangeStatusRequest
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.utils.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmergencyDetailsFragment : Fragment(R.layout.emergency_details_fragment) {

    private var emergency: Emergency? = null
    private var addNewEmergencyFlag: Boolean? = null
    private lateinit var preferenceManager: PreferenceManager

    private lateinit var finishEmergencyView: MaterialCardView
    private lateinit var takeOnAEmergencyView: MaterialCardView
    private lateinit var sendEmergencyView: MaterialCardView
    private lateinit var patientView: MaterialCardView
    private lateinit var paramedicView: MaterialCardView
    private lateinit var noParamedicView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emergency = arguments?.getSerializable("emergency") as? Emergency
        addNewEmergencyFlag = arguments?.getSerializable("addNewEmergencyFlag") as? Boolean

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()

        finishEmergencyView = view.findViewById(R.id.finishEmergencyView)
        takeOnAEmergencyView = view.findViewById(R.id.takeOnAEmergencyView)
        sendEmergencyView = view.findViewById(R.id.sendEmergencyView)
        patientView = view.findViewById(R.id.patientView)
        paramedicView = view.findViewById(R.id.paramedicView)
        noParamedicView = view.findViewById(R.id.noParamedicView)

        finishEmergencyView.setOnClickListener {
            showFinishEmergencyDialog()
        }

        takeOnAEmergencyView.setOnClickListener {
            showTakeOnEmergencyDialog()
        }

        sendEmergencyView.setOnClickListener {
            showAddNewEmergencyDialog()
        }

        if (userRole == "Paramedic") setParamedicLayout()
        else setPatientLayout()
    }

    private fun setMainView(){
        setDate()
        setAddressData()
        setDescription()
        setStatus()
    }

    private fun setParamedicView(){
        setParamedicData()
    }

    private fun setPatientView(){
        setPatientData()
    }

    private fun setPatientLayout(){
        if (addNewEmergencyFlag == true){
            setPatientView()
            patientView.visibility = View.VISIBLE
            paramedicView.visibility = View.GONE
            noParamedicView.visibility = View.GONE
        }
        else {
            patientView.visibility = View.GONE
            if (emergency?.paramedic == null){
                noParamedicView.visibility = View.VISIBLE
                paramedicView.visibility = View.GONE
            }
            else{
                setParamedicView()
                noParamedicView.visibility = View.GONE
                paramedicView.visibility = View.VISIBLE
            }

        }
        setMainView()
    }

    private fun setParamedicLayout(){
        patientView.visibility = View.VISIBLE
        paramedicView.visibility = View.GONE
        noParamedicView.visibility = View.GONE

        setPatientView()
        setMainView()
    }

    private fun setPatientData(){
        view?.findViewById<TextView>(R.id.firstNamePatientTextView)?.text = "${emergency?.patient?.firstName}"
        view?.findViewById<TextView>(R.id.lastNamePatientTextView)?.text = "${emergency?.patient?.lastName}"
        val phoneNumberConverted = "${emergency?.patient?.phoneNumber?.substring(0,3)}-${emergency?.patient?.phoneNumber?.substring(3,6)}-${emergency?.patient?.phoneNumber?.substring(6)}"
        view?.findViewById<TextView>(R.id.phoneNumberPatientTextView)?.text = "${phoneNumberConverted}"
        view?.findViewById<TextView>(R.id.emailPatientTextView)?.text = "${emergency?.patient?.email}"
    }

    private fun setParamedicData(){
        view?.findViewById<TextView>(R.id.firstNameParamedicTextView)?.text = "${emergency?.paramedic?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameParamedicTextView)?.text = "${emergency?.paramedic?.lastName}"
        view?.findViewById<TextView>(R.id.specializationParamedicTextView)?.text = "Ratownik medyczny"
        view?.findViewById<TextView>(R.id.hospitalParamedicTextView)?.text = "${emergency?.paramedic?.workPlace?.name}"
    }

    private fun setDate(){
        val startDateTextView: TextView? = view?.findViewById(R.id.startDateTextView)
        val startHourTextView : TextView? = view?.findViewById(R.id.startHourTextView)

        setDate(startDateTextView, startHourTextView, emergency?.emergencyReportTime)
    }


    private fun setAddressData(){
        val cityEmergencyTextView =  view?.findViewById<TextView>(R.id.cityEmergencyTextView)
        val postalCodeEmergencyTextView =  view?.findViewById<TextView>(R.id.postalCodeEmergencyTextView)
        val streetEmergencyTextView =  view?.findViewById<TextView>(R.id.streetEmergencyTextView)

        setAddress(emergency?.address, cityEmergencyTextView, postalCodeEmergencyTextView, streetEmergencyTextView)
    }

    private fun setDescription(){
        view?.findViewById<TextView>(R.id.descriptionTextView)?.text = "${emergency?.description}"
    }

    private fun setStatus(){
        if (emergency?.status == "Available" && preferenceManager.getRole() == "Paramedic"){
            takeOnAEmergencyView.visibility = View.VISIBLE
        }
        else takeOnAEmergencyView.visibility = View.GONE

        if (emergency?.status == "In_progress" && preferenceManager.getRole() == "Paramedic") {
            finishEmergencyView.visibility = View.VISIBLE
        }
        else finishEmergencyView.visibility = View.GONE

        if (emergency?.status == "Available" && preferenceManager.getRole() != "Paramedic"
            && addNewEmergencyFlag == true){
            sendEmergencyView.visibility = View.VISIBLE
        }
        else sendEmergencyView.visibility = View.GONE
    }

    private fun showTakeOnEmergencyDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz się podjąć zgłoszenia?"){
            sendTakeOnEmergencyRequest()
        }
    }

    private fun finishEmergency(){
        sendFinishEmergencyRequest()
    }

    private fun showFinishEmergencyDialog(){
        showDialog(requireContext(),"Czy na pewno zgłoszenie można uznać za zakończone?"){
            finishEmergency()
        }
    }

    private fun showAddNewEmergencyDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz dodać te zgłoszenie?"){
            sendNewEmergency()
        }
    }

    private fun navigateToEmergencyFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyFragment())
    }

    private fun sendNewEmergency(){
        val token = "Bearer " + preferenceManager.getAuthToken()
        val emergencyRequest = Emergency(emergency?.id, emergency?.patient, emergency?.paramedic,
            emergency?.status, emergency?.emergencyReportTime, emergency?.address, emergency?.description)

        AppointmentRetrofitInstance.appointmentApiService.addNewEmergency(token,
            emergencyRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Zgłoszenie zostało dodane pomyślnie.", Toast.LENGTH_LONG).show()
                    navigateToEmergencyFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Podczas tworzenia zgłoszenia wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendTakeOnEmergencyRequest(){
        val token = "Bearer " + preferenceManager.getAuthToken()
        AppointmentRetrofitInstance.appointmentApiService.assignEmergency(token,
            emergency?.id.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Zgłoszenie zostało przypisane pomyślnie.", Toast.LENGTH_LONG).show()
                    navigateToEmergencyFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Podczas przypisywania zgłoszenia wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendFinishEmergencyRequest(){
        val token = "Bearer " + preferenceManager.getAuthToken()
        val emergencyChangeStatusRequest = EmergencyChangeStatusRequest("Completed")

        AppointmentRetrofitInstance.appointmentApiService.finishEmergency(token,
            emergency?.id.toString(), emergencyChangeStatusRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Zgłoszenie zostało zakończone pomyślnie.", Toast.LENGTH_LONG).show()
                    navigateToEmergencyFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Podczas zakończenia zgłoszenia wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}