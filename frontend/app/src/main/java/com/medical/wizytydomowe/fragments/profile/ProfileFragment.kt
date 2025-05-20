package com.medical.wizytydomowe.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.MainActivity
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.authApi.AuthRetrofitInstance
import retrofit2.Callback
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import retrofit2.Call
import retrofit2.Response
import com.medical.wizytydomowe.api.utils.*
import com.medical.wizytydomowe.fragments.editProfile.EditPasswordFragment
import com.medical.wizytydomowe.fragments.editProfile.EditProfileFragment

class ProfileFragment : Fragment(R.layout.profile_fragment) {

    private var userEmail: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()
        val userRole = preferenceManager.getRole()

        val logoutView = view.findViewById<MaterialCardView>(R.id.logoutView)
        val editProfileView = view.findViewById<MaterialCardView>(R.id.editProfileView)
        val editPasswordView = view.findViewById<MaterialCardView>(R.id.editPasswordView)

        if (userToken != null) {
            if (userRole == "Patient") setPatientLayout()
            else setMedicalStaffLayout()

            val requestToken = "Bearer $userToken"
            sendRequest(requestToken, userRole)
        }
        else{
            (activity as? MainActivity)?.setMenuForUser(PreferenceManager(requireContext()))
            navigateToLoginFragment()
        }

        logoutView.setOnClickListener {
            preferenceManager.clearAuthToken()
            preferenceManager.clearRole()

            Toast.makeText(context, "Wylogowano pomyślnie.", Toast.LENGTH_SHORT).show()

            (activity as? MainActivity)?.setMenuForUser(PreferenceManager(requireContext()))
            navigateToLoginFragment()
        }

        editProfileView.setOnClickListener {
            navigateToEditProfileFragment()
        }

        editPasswordView.setOnClickListener {
            navigateToEditPasswordFragment()
        }
    }

    private fun setPatientLayout(){
        view?.findViewById<MaterialCardView>(R.id.specializationView)?.visibility = View.GONE
        view?.findViewById<MaterialCardView>(R.id.hospitalView)?.visibility = View.GONE
        view?.findViewById<MaterialCardView>(R.id.dateOfBirthView)?.visibility = View.VISIBLE
        view?.findViewById<MaterialCardView>(R.id.addressView)?.visibility = View.VISIBLE
    }

    private fun setMedicalStaffLayout(){
        view?.findViewById<MaterialCardView>(R.id.specializationView)?.visibility = View.VISIBLE
        view?.findViewById<MaterialCardView>(R.id.hospitalView)?.visibility = View.VISIBLE
        view?.findViewById<MaterialCardView>(R.id.dateOfBirthView)?.visibility = View.GONE
        view?.findViewById<MaterialCardView>(R.id.addressView)?.visibility = View.GONE
    }

    private fun sendRequest(requestToken: String, userRole: String?){
        AuthRetrofitInstance.authApiService.getUserInfo(requestToken)
            .enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        setRole(userRole)
                        setAppropriateRoleImage(userRole)
                        userEmail = responseBody?.email

                        if (userRole == "Patient") setPatientView(responseBody)
                        else setMedicalStaffView(responseBody)
                    } else {
                        Log.e("API", "Błąd: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    Log.e("API", "Niepowodzenie: ${t.message}")
                }
            })
    }

    private fun navigateToLoginFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(LoginFragment())
    }

    private fun navigateToEditProfileFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EditProfileFragment())
    }

    private fun setAddress(address: String?){
        val cityTextView = view?.findViewById<TextView>(R.id.cityTextView)
        val postalCodeTextView = view?.findViewById<TextView>(R.id.postalCodeTextView)
        val streetTextView = view?.findViewById<TextView>(R.id.streetTextView)

        setAddress(address, cityTextView, postalCodeTextView, streetTextView)
    }

    private fun setDateOfBirth(dateOfBirth: String?){
        val dateOfBirthConverted = convertToDateFormat(dateOfBirth, "yyyy-MM-dd", "dd-MM-yyyy")
        if (!dateOfBirthConverted.isNullOrEmpty()) view?.findViewById<TextView>(R.id.dateOfBirthTextView)?.text = "${dateOfBirthConverted}"
        else view?.findViewById<TextView>(R.id.dateOfBirthTextView)?.text = "None"
    }

    private fun setPersonalData(userInfo: UserInfoResponse?){
        view?.findViewById<TextView>(R.id.firstNameTextView)?.text = "${userInfo?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameTextView)?.text = "${userInfo?.lastName}"
        view?.findViewById<TextView>(R.id.emailTextView)?.text = "${userInfo?.email}"
        val phoneNumberConverted = "${userInfo?.phoneNumber?.substring(0,3)}-${userInfo?.phoneNumber?.substring(3,6)}-${userInfo?.phoneNumber?.substring(6)}"
        view?.findViewById<TextView>(R.id.phoneNumberTextView)?.text = "${phoneNumberConverted}"
    }

    private fun setRole(userRole: String?){
        if (userRole == "Patient") view?.findViewById<TextView>(R.id.roleTextView)?.text = "Pacjent"
        if (userRole == "Doctor") view?.findViewById<TextView>(R.id.roleTextView)?.text = "Lekarz"
        if (userRole == "Nurse") view?.findViewById<TextView>(R.id.roleTextView)?.text = "Pielęgniarka"
        if (userRole == "Paramedic") view?.findViewById<TextView>(R.id.roleTextView)?.text = "Ratownik medyczny"
    }

    private fun setPatientImage(){
        view?.findViewById<ImageView>(R.id.patientImage)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.doctorImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.nurseImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.paramedicImage)?.visibility = View.GONE
    }

    private fun setDoctorImage(){
        view?.findViewById<ImageView>(R.id.patientImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.doctorImage)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.nurseImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.paramedicImage)?.visibility = View.GONE
    }

    private fun setNurseImage(){
        view?.findViewById<ImageView>(R.id.patientImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.doctorImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.nurseImage)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.paramedicImage)?.visibility = View.GONE
    }

    private fun setParamedicImage(){
        view?.findViewById<ImageView>(R.id.patientImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.doctorImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.nurseImage)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.paramedicImage)?.visibility = View.VISIBLE
    }

    private fun setAppropriateRoleImage(userRole: String?){
        if (userRole == "Patient") setPatientImage()
        if (userRole == "Doctor") setDoctorImage()
        if (userRole == "Nurse") setNurseImage()
        if (userRole == "Paramedic") setParamedicImage()
    }

    private fun setPatientView(userInfo: UserInfoResponse?){
        setPersonalData(userInfo)
        setDateOfBirth(userInfo?.dateOfBirth)
        setAddress(userInfo?.address)
    }

    private fun setMedicalData(userInfo: UserInfoResponse?){
        view?.findViewById<TextView>(R.id.hospitalTextView)?.text = "${userInfo?.workPlace?.name}"
        view?.findViewById<TextView>(R.id.specializationTextView)?.text = "${userInfo?.specialization}"
    }

    private fun setMedicalStaffView(userInfo: UserInfoResponse?){
        setPersonalData(userInfo)
        setMedicalData(userInfo)
    }

    private fun navigateToEditPasswordFragment(){
        val bundle = Bundle().apply { putSerializable("userEmail", userEmail) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EditPasswordFragment().apply { arguments = bundle })
    }

}