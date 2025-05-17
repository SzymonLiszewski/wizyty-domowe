package com.medical.wizytydomowe.fragments.editProfile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import com.medical.wizytydomowe.api.userInfo.EditUserInfoResponse
import com.medical.wizytydomowe.api.userInfo.EditUserInfoRequest
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import com.medical.wizytydomowe.api.utils.*
import com.medical.wizytydomowe.fragments.profile.ProfileFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFormsFragment : Fragment(R.layout.edit_profile_forms_fragment) {

    private var editUserInfoRequest: EditUserInfoRequest? = null
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var requestToken: String

    private lateinit var editType: String
    private lateinit var editPersonalDataView: MaterialCardView
    private lateinit var editAddressFormView: MaterialCardView
    private lateinit var editContactDataView: MaterialCardView
    private lateinit var editDateOfBirthView: MaterialCardView

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null
    private var dateOfBirth: String? = null
    private var city: String? = null
    private var street: String? = null
    private var postalCode: String? = null
    private var apartmentNumber: String? = null
    private var buildingNumber: String? = null
    private var address: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editType = arguments?.getSerializable("editType") as String

        preferenceManager = PreferenceManager(requireContext())
        requestToken = "Bearer ${preferenceManager.getAuthToken()}"
        sendUserInfoRequest(requestToken)

        editPersonalDataView = view.findViewById(R.id.editPersonalDataView)
        editAddressFormView = view.findViewById(R.id.editAddressFormView)
        editContactDataView = view.findViewById(R.id.editContactDataView)
        editDateOfBirthView = view.findViewById(R.id.editDateOfBirthView)

        val buttonPrevEditPersonalData = view.findViewById<Button>(R.id.buttonPrevEditPersonalData)
        val buttonResetPersonalData = view.findViewById<Button>(R.id.buttonResetPersonalData)
        val buttonPrevEditAddress = view.findViewById<Button>(R.id.buttonPrevEditAddress)
        val buttonResetAddress = view.findViewById<Button>(R.id.buttonResetAddress)
        val buttonPrevEditContactData = view.findViewById<Button>(R.id.buttonPrevEditContactData)
        val buttonResetContactData = view.findViewById<Button>(R.id.buttonResetContactData)
        val buttonPrevEditDateOfBirth = view.findViewById<Button>(R.id.buttonPrevEditDateOfBirth)
        val buttonResetDateOfBirth = view.findViewById<Button>(R.id.buttonResetDateOfBirth)

        buttonPrevEditDateOfBirth.setOnClickListener { navigateToEditProfileFragment() }
        buttonPrevEditContactData.setOnClickListener { navigateToEditProfileFragment() }
        buttonPrevEditAddress.setOnClickListener { navigateToEditProfileFragment() }
        buttonPrevEditPersonalData.setOnClickListener { navigateToEditProfileFragment() }

        buttonResetDateOfBirth.setOnClickListener { validateAndEditDateOfBirth() }
        buttonResetContactData.setOnClickListener { validateAndEditContact() }
        buttonResetAddress.setOnClickListener { validateAndEditAddress() }
        buttonResetPersonalData.setOnClickListener { validateAndEditPersonalDataFields() }

        if (editType == "personalData") setEditPersonalDataLayout()
        else if (editType == "contact") setEditContactLayout()
        else if (editType == "address") setEditAddressLayout()
        else if (editType == "dateOfBirth") setEditDateOfBirthLayout()
    }

    private fun setEditPersonalDataLayout(){
        editPersonalDataView.visibility = View.VISIBLE
        editAddressFormView.visibility = View.GONE
        editContactDataView.visibility = View.GONE
        editDateOfBirthView.visibility = View.GONE
    }

    private fun setEditContactLayout(){
        editPersonalDataView.visibility = View.GONE
        editAddressFormView.visibility = View.GONE
        editContactDataView.visibility = View.VISIBLE
        editDateOfBirthView.visibility = View.GONE
    }

    private fun setEditAddressLayout(){
        editPersonalDataView.visibility = View.GONE
        editAddressFormView.visibility = View.VISIBLE
        editContactDataView.visibility = View.GONE
        editDateOfBirthView.visibility = View.GONE
    }

    private fun setEditDateOfBirthLayout(){
        editPersonalDataView.visibility = View.GONE
        editAddressFormView.visibility = View.GONE
        editContactDataView.visibility = View.GONE
        editDateOfBirthView.visibility = View.VISIBLE
    }

    private fun navigateToEditProfileFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EditProfileFragment())
    }

    private fun validateAndEditPersonalDataFields(){
        firstName = view?.findViewById<TextInputEditText>(R.id.textInputEditTextFirstName)?.text.toString()
        lastName = view?.findViewById<TextInputEditText>(R.id.textInputEditTextLastName)?.text.toString()

        if (validateNewPersonalData(firstName, lastName)){
            editUserInfoRequest?.firstName = firstName
            editUserInfoRequest?.lastName = lastName

            if (editUserInfoRequest != null) sendEditUserDataRequest(editUserInfoRequest)
            else Toast.makeText(requireContext(), "Wystąpił błąd podczas zmiany danych.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateNewPersonalData(firstName: String?, lastName: String?): Boolean{
        val firstNameLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutFirstName)
        val lastNameLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutLastName)

        return validatePersonalDataFields(firstName, lastName, firstNameLayout, lastNameLayout)
    }

    private fun validateAndEditAddress(){
        city = view?.findViewById<TextInputEditText>(R.id.textInputEditTextCity)?.text.toString()
        street = view?.findViewById<TextInputEditText>(R.id.textInputEditTextStreet)?.text.toString()
        postalCode = view?.findViewById<TextInputEditText>(R.id.textInputEditTextPostalCode)?.text.toString()
        apartmentNumber = view?.findViewById<TextInputEditText>(R.id.textInputEditTextApartmentNumber)?.text.toString()
        buildingNumber = view?.findViewById<TextInputEditText>(R.id.textInputEditTextBuildingNumber)?.text.toString()

        if (validateNewAddress(city, street, postalCode, buildingNumber)){
            if (apartmentNumber.isNullOrEmpty()) address = "${city}, ${postalCode}, ${street} ${buildingNumber}"
            else address = "${city}, ${postalCode}, ${street} ${buildingNumber}/${apartmentNumber}"

            editUserInfoRequest?.address = address
            if (editUserInfoRequest != null) sendEditUserDataRequest(editUserInfoRequest)
            else Toast.makeText(requireContext(), "Wystąpił błąd podczas zmiany danych.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateNewAddress(city: String?, street: String?, postalCode: String?,
                                    buildingNumber: String?): Boolean{
        val cityLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutCity)
        val streetLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutStreet)
        val postalCodeLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPostalCode)
        val buildingNumberLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutBuildingNumber)

        return validateAddress(city, street, buildingNumber, postalCode, cityLayout, streetLayout,
            postalCodeLayout, buildingNumberLayout)
    }

    private fun validateAndEditContact(){
        email = view?.findViewById<TextInputEditText>(R.id.textInputEditTextEmail)?.text.toString()
        phoneNumber = view?.findViewById<TextInputEditText>(R.id.textInputEditTextPhoneNumber)?.text.toString()

        if (validateNewContactFields(email, phoneNumber)){
            editUserInfoRequest?.email = email
            editUserInfoRequest?.phoneNumber = phoneNumber

            if (editUserInfoRequest != null) sendEditUserDataRequest(editUserInfoRequest)
            else Toast.makeText(requireContext(), "Wystąpił błąd podczas zmiany danych.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateNewContactFields(email: String?, phoneNumber: String?): Boolean{
        val emailLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val phoneNumberLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPhoneNumber)

        return validateContactFields(email, phoneNumber, emailLayout, phoneNumberLayout)
    }

    private fun validateAndEditDateOfBirth(){
        dateOfBirth = view?.findViewById<TextInputEditText>(R.id.textInputEditTextDateOfBirth)?.text.toString()

        if (validateNewDateOfBirth(dateOfBirth)){
            val dateOfBirthRequest = convertToDateFormat(dateOfBirth, "dd-MM-yyyy", "yyyy-MM-dd")
            if (dateOfBirthRequest == null) {
                Toast.makeText(context, "Wystąpił błąd podczas zmiany danych.", Toast.LENGTH_SHORT).show()
            }
            else{
                editUserInfoRequest?.dateOfBirth = dateOfBirthRequest

                if (editUserInfoRequest != null) sendEditUserDataRequest(editUserInfoRequest)
                else Toast.makeText(requireContext(), "Wystąpił błąd podczas zmiany danych.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateNewDateOfBirth(dateOfBirth: String?): Boolean{
        val dateOfBirthLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutDateOfBirth)

        return validateDateOfBirth(dateOfBirth, dateOfBirthLayout)
    }

    private fun navigateToProfileFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(ProfileFragment())
    }

    private fun sendUserInfoRequest(requestToken: String) {
        RetrofitInstance.apiService.getUserInfo(requestToken)
            .enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(
                    call: Call<UserInfoResponse>,
                    response: Response<UserInfoResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        editUserInfoRequest = EditUserInfoRequest(responseBody?.firstName,
                            responseBody?.lastName, responseBody?.email, responseBody?.phoneNumber,
                            responseBody?.dateOfBirth, responseBody?.address)
                    } else {
                        Log.e("API", "Błąd: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    Log.e("API", "Niepowodzenie: ${t.message}")
                }
            })
    }

    private fun sendEditUserDataRequest(editUserInfoRequest: EditUserInfoRequest?){
        RetrofitInstance.apiService.editUserData(editUserInfoRequest, requestToken).enqueue(object :
            Callback<EditUserInfoResponse> {
            override fun onResponse(call: Call<EditUserInfoResponse>, response: Response<EditUserInfoResponse>) {
                if (response.isSuccessful) {
                    val editUserInfoResponse = response.body()

                    if (preferenceManager.setTokenAndRole(editUserInfoResponse?.token,
                        editUserInfoResponse?.refresh_token, editUserInfoResponse?.role)){
                        Toast.makeText(context, "Zmiana danych przebiegła pomyślnie.", Toast.LENGTH_LONG).show()
                        navigateToProfileFragment()
                    }
                    else Toast.makeText(context, "Zmiana danych nie powiodła się.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(context, "Zmiana danych nie powiodła się.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EditUserInfoResponse>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}