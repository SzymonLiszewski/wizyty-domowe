package com.medical.wizytydomowe.fragments.emergency

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.users.Patient
import com.medical.wizytydomowe.api.utils.validateContactFields
import com.medical.wizytydomowe.api.utils.validateDescription
import com.medical.wizytydomowe.api.utils.validatePersonalDataFields
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEmergencyFragment : Fragment(R.layout.add_emergency_fragment)  {

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var personalDataView: MaterialCardView
    private lateinit var descriptionView: MaterialCardView

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null
    private var description: String? = null

    private var pageNumber: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()

        personalDataView = view.findViewById(R.id.personalDataView)
        descriptionView = view.findViewById(R.id.descriptionView)

        val btn_next1 = view.findViewById<Button>(R.id.btn_next1)
        val buttonPrevLogout = view.findViewById<Button>(R.id.buttonPrevLogout)
        val buttonAddEmergencyLogout = view.findViewById<Button>(R.id.buttonAddEmergencyLogout)
        val addEmergencyButtonLogin = view.findViewById<Button>(R.id.addEmergencyButtonLogin)

        btn_next1.setOnClickListener {
            firstName = view.findViewById<TextInputEditText>(R.id.textInputEditTextFirstName).text.toString()
            lastName = view.findViewById<TextInputEditText>(R.id.textInputEditTextLastName).text.toString()
            email = view.findViewById<TextInputEditText>(R.id.textInputEditTextEmail).text.toString()
            phoneNumber = view.findViewById<TextInputEditText>(R.id.textInputEditTextPhoneNumber).text.toString()

            if (validateFieldsPage1(firstName, lastName, email, phoneNumber)){
                pageNumber += 1
                setPage(pageNumber)
            }
        }

        buttonPrevLogout.setOnClickListener {
            pageNumber -= 1
            setPage(pageNumber)
        }

        buttonAddEmergencyLogout.setOnClickListener {
            description = view.findViewById<TextInputEditText>(R.id.textInputEditTextDescription).text.toString()

            if (validateFieldsPage2(description)){
                navigateToConfirmEmergency()
            }
        }

        addEmergencyButtonLogin.setOnClickListener {
            description = view.findViewById<TextInputEditText>(R.id.textInputEditTextDescription).text.toString()

            if (validateFieldsPage2(description)){
                navigateToConfirmEmergency()
            }
        }

        if (userToken != null) pageNumber = 2
        else pageNumber = 1

        setPage(pageNumber)
    }

    private fun setPage(pageNumber: Int){
        if (pageNumber == 1){
            personalDataView.visibility = View.VISIBLE
            descriptionView.visibility = View.GONE
        }
        else{
            personalDataView.visibility = View.GONE
            descriptionView.visibility = View.VISIBLE

            if (preferenceManager.getAuthToken() != null){
                view?.findViewById<LinearLayout>(R.id.linearLayoutButtonsDescription)?.visibility = View.GONE
                view?.findViewById<Button>(R.id.addEmergencyButtonLogin)?.visibility = View.VISIBLE
            }
            else {
                view?.findViewById<LinearLayout>(R.id.linearLayoutButtonsDescription)?.visibility = View.VISIBLE
                view?.findViewById<Button>(R.id.addEmergencyButtonLogin)?.visibility = View.GONE
            }
        }
    }

    private fun validateFieldsPage1(firstName: String?, lastName: String?, email: String?,
                                    phoneNumber: String?): Boolean{
        val firstNameLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutFirstName)
        val lastNameLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutLastName)
        val emailLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val phoneNumberLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPhoneNumber)

        if (!validatePersonalDataFields(firstName, lastName, firstNameLayout, lastNameLayout)) return false
        if (!validateContactFields(email, phoneNumber, emailLayout, phoneNumberLayout)) return false
        return true
    }

    private fun validateFieldsPage2(description: String?): Boolean{
        val descriptionLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutDescription)

        if (!validateDescription(description, descriptionLayout)) return false
        return true
    }

    private fun setActualDate(): String{
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH.mm", Locale.getDefault())
        val currentDate = Date()
        return outputFormat.format(currentDate)
    }

    private fun navigateToConfirmEmergency(){
        val patient: Patient
        if (preferenceManager.getAuthToken() != null){
            //TODO sent request for userInfo
            patient = Patient("0", "Jan", "Rogowski", "jan@rog.pl", "123-456-789")
        }
        else {
            patient = Patient("0", firstName, lastName, email, phoneNumber)
        }

        //TODO set address as phone localization
        val address = "Gdańsk, 10-101, Gdańska 12A/4"
        val emergency = Emergency("0",  patient, null, "AVAILABLE", setActualDate(), address, description)
        navigateToEmergencyDetailsFragment(emergency)
    }

    private fun navigateToEmergencyDetailsFragment(emergency: Emergency){
        val bundle = Bundle().apply {
            putSerializable("emergency", emergency)
            putSerializable("addNewEmergencyFlag", true)
        }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyDetailsFragment().apply { arguments = bundle })
    }
}