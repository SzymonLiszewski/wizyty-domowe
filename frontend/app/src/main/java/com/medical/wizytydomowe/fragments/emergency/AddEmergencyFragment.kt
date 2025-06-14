package com.medical.wizytydomowe.fragments.emergency

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.authApi.AuthRetrofitInstance
import com.medical.wizytydomowe.api.editProfile.EditUserInfoRequest
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import com.medical.wizytydomowe.api.users.Patient
import com.medical.wizytydomowe.api.utils.validateContactFields
import com.medical.wizytydomowe.api.utils.validateDescription
import com.medical.wizytydomowe.api.utils.validatePersonalDataFields
import com.medical.wizytydomowe.api.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class AddEmergencyFragment : Fragment(R.layout.add_emergency_fragment)  {

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var personalDataView: MaterialCardView
    private lateinit var descriptionView: MaterialCardView

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentAddress: String? = null

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null
    private var description: String? = null
    private var userInfoResponse: UserInfoResponse? = null

    private var pageNumber: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getUserLocationAndAddress()
        }

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

        if (userToken != null){
            pageNumber = 2
            sendUserInfoRequest("Bearer " + userToken)
        }
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

    private fun getUserLocationAndAddress() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            currentAddress = "Brak uprawnień do lokalizacji"
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        currentAddress = addresses[0].getAddressLine(0)
                    } else {
                        currentAddress = "Nieznany adres"
                    }
                } catch (e: IOException) {
                    currentAddress = "Błąd geokodowania"
                }
            } else {
                currentAddress = "Lokalizacja niedostępna"
            }
        }.addOnFailureListener {
            currentAddress = "Błąd pobierania lokalizacji"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocationAndAddress()
            } else {
                Toast.makeText(requireContext(), "Brak zgody na lokalizację", Toast.LENGTH_SHORT).show()
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

    private fun navigateToConfirmEmergency(){
        val patient: Patient?
        if (preferenceManager.getAuthToken() != null){
            if (userInfoResponse != null) patient = Patient(userInfoResponse?.id, userInfoResponse?.firstName, userInfoResponse?.lastName,
                userInfoResponse?.email, userInfoResponse?.phoneNumber)
            else patient = null
        }
        else {
            patient = Patient("0", firstName, lastName, email, phoneNumber)
        }

        if (patient != null){
            val address = currentAddress ?: "Adres niedostępny"
            val emergency = Emergency(null,  patient, null, "Available", setActualDate(), address, description)
            navigateToEmergencyDetailsFragment(emergency)
        }
    }

    private fun navigateToEmergencyDetailsFragment(emergency: Emergency){
        val bundle = Bundle().apply {
            putSerializable("emergency", emergency)
            putSerializable("addNewEmergencyFlag", true)
        }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyDetailsFragment().apply { arguments = bundle })
    }

    private fun sendUserInfoRequest(requestToken: String) {
        AuthRetrofitInstance.authApiService.getUserInfo(requestToken)
            .enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(
                    call: Call<UserInfoResponse>,
                    response: Response<UserInfoResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        userInfoResponse = UserInfoResponse(responseBody?.id, responseBody?.firstName,
                            responseBody?.lastName, responseBody?.email, null, null,
                            null, null, null, responseBody?.phoneNumber)
                    } else {
                        Log.e("API", "Błąd: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    Log.e("API", "Niepowodzenie: ${t.message}")
                }
            })
    }
}