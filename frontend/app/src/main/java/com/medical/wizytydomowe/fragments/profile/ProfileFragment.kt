package com.medical.wizytydomowe.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.MainActivity
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import retrofit2.Callback
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileFragment : Fragment(R.layout.profile_fragment) {

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
            moveToLoginFragment()
        }

        logoutView.setOnClickListener {
            preferenceManager.clearAuthToken()
            preferenceManager.clearRole()

            Toast.makeText(context, "Wylogowano pomyślnie.", Toast.LENGTH_SHORT).show()

            (activity as? MainActivity)?.setMenuForUser(PreferenceManager(requireContext()))
            moveToLoginFragment()
        }

        editProfileView.setOnClickListener {
            Toast.makeText(context, "Kliknięto edycję profilu.", Toast.LENGTH_SHORT).show()
        }

        editPasswordView.setOnClickListener {
            Toast.makeText(context, "Kliknięto edycję hasła.", Toast.LENGTH_SHORT).show()
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
        RetrofitInstance.apiService.getUserInfo(requestToken)
            .enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        setRole(userRole)
                        setAppropiateRoleImage(userRole)

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

    private fun moveToLoginFragment(){
        val loginFragment = LoginFragment()

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(loginFragment)
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

    private fun setAddress(address: String?){
        if (!address.isNullOrEmpty()){
            val parts = address.split(",".toRegex(), 3)
            if (parts.size == 3) {
                view?.findViewById<TextView>(R.id.cityTextView)?.text = parts[0].trim()
                view?.findViewById<TextView>(R.id.postalCodeTextView)?.text = parts[1].trim()
                view?.findViewById<TextView>(R.id.streetTextView)?.text = "ul. " + parts[2].trim()
            }
        }
        else{
            view?.findViewById<TextView>(R.id.cityTextView)?.text = "None"
            view?.findViewById<TextView>(R.id.postalCodeTextView)?.text = "None"
            view?.findViewById<TextView>(R.id.streetTextView)?.text = "None"
        }
    }

    private fun setDateOfBirth(dateOfBirth: String?){
        val dateOfBirthConverted = convertToDateFormat(dateOfBirth)
        if (!dateOfBirthConverted.isNullOrEmpty()) view?.findViewById<TextView>(R.id.dateOfBirthTextView)?.text = "${dateOfBirthConverted}"
        else view?.findViewById<TextView>(R.id.dateOfBirthTextView)?.text = "None"
    }

    private fun setPersonalData(userInfo: UserInfoResponse?){
        view?.findViewById<TextView>(R.id.firstNameTextView)?.text = "${userInfo?.firstName}"
        view?.findViewById<TextView>(R.id.lastNameTextView)?.text = "${userInfo?.lastName}"
        view?.findViewById<TextView>(R.id.emailTextView)?.text = "${userInfo?.email}"
        view?.findViewById<TextView>(R.id.phoneNumberTextView)?.text = "123-456-789"
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

    private fun setAppropiateRoleImage(userRole: String?){
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
        view?.findViewById<TextView>(R.id.hospitalTextView)?.text = "${userInfo?.workPlace}"
        view?.findViewById<TextView>(R.id.specializationTextView)?.text = "${userInfo?.specialization}"
    }

    private fun setMedicalStaffView(userInfo: UserInfoResponse?){
        setPersonalData(userInfo)
        setMedicalData(userInfo)
    }

}