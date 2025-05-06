package com.medical.wizytydomowe.fragments.emergency

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.utils.*

class EmergencyDetailsFragment : Fragment(R.layout.emergency_details_fragment) {

    private var emergency: Emergency? = null
    private lateinit var preferenceManager: PreferenceManager

    private lateinit var finishEmergencyView: MaterialCardView
    private lateinit var takeOnAEmergencyView: MaterialCardView
    private lateinit var patientView: MaterialCardView
    private lateinit var paramedicView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emergency = arguments?.getSerializable("emergency") as? Emergency
        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()

        finishEmergencyView = view.findViewById(R.id.finishEmergencyView)
        takeOnAEmergencyView = view.findViewById(R.id.takeOnAEmergencyView)
        patientView = view.findViewById(R.id.patientView)
        paramedicView = view.findViewById(R.id.paramedicView)

        finishEmergencyView.setOnClickListener {
            showFinishEmergencyDialog()
        }

        takeOnAEmergencyView.setOnClickListener {
            showTakeOnEmergencyDialog()
        }

        if (userRole == "Patient") setPatientLayout()
        else setParamedicLayout()

    }

    private fun setMainView(){
        setDate()
        setAddressData()
        setDescription()
        setStatus()
    }

    private fun setParamedicView(){
        setPatientData()
    }

    private fun setPatientView(){
        setParamedicData()
    }

    private fun setPatientLayout(){
        patientView.visibility = View.GONE
        paramedicView.visibility = View.VISIBLE

        setPatientView()
        setMainView()
    }

    private fun setParamedicLayout(){
        patientView.visibility = View.VISIBLE
        paramedicView.visibility = View.GONE

        setParamedicView()
        setMainView()
    }

    private fun setPatientData(){
        view?.findViewById<TextView>(R.id.firstNamePatientTextView)?.text = "${emergency?.patient?.firstName}"
        view?.findViewById<TextView>(R.id.lastNamePatientTextView)?.text = "${emergency?.patient?.lastName}"
        view?.findViewById<TextView>(R.id.phoneNumberPatientTextView)?.text = "123-456-789"
        view?.findViewById<TextView>(R.id.emailPatientTextView)?.text = "janrogowski@gmail.com"
    }

    private fun setParamedicData(){
        view?.findViewById<TextView>(R.id.firstNameParamedicTextView)?.text = "${emergency?.paramedic?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameParamedicTextView)?.text = "${emergency?.paramedic?.lastName}"
        view?.findViewById<TextView>(R.id.specializationParamedicTextView)?.text = "Ratownik medyczny"
        view?.findViewById<TextView>(R.id.hospitalParamedicTextView)?.text = "${emergency?.paramedic?.workPlace}"
    }

    private fun setDate(){
        val startDateTextView: TextView? = view?.findViewById(R.id.startDateTextView)
        val startHourTextView : TextView? = view?.findViewById(R.id.startHourTextView)

        setDate(startDateTextView, startHourTextView, emergency?.date)
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
        if (emergency?.status == "AVAILABLE" && preferenceManager.getRole() == "Paramedic"){
            takeOnAEmergencyView.visibility = View.VISIBLE
        }
        else takeOnAEmergencyView.visibility = View.GONE

        if (emergency?.status == "IN PROGRESS" && preferenceManager.getRole() == "Paramedic") {
            finishEmergencyView.visibility = View.VISIBLE
        }
        else finishEmergencyView.visibility = View.GONE
    }

    private fun takeOnEmergency(){
        //TODO send request to the backend and toast
        Toast.makeText(requireContext(), "Podjęto się zgłoszenia.", Toast.LENGTH_SHORT).show()
    }

    private fun showTakeOnEmergencyDialog(){
        showDialog(requireContext(),"Czy na pewno chcesz się podjąć zgłoszenia?"){
            takeOnEmergency()
        }
    }

    private fun finishEmergency(){
        //TODO send request to the backend and toast
        Toast.makeText(requireContext(), "Zakończono zgłoszenie.", Toast.LENGTH_SHORT).show()
    }

    private fun showFinishEmergencyDialog(){
        showDialog(requireContext(),"Czy na pewno zgłoszenie można uznać za zakończone?"){
            finishEmergency()
        }
    }
}