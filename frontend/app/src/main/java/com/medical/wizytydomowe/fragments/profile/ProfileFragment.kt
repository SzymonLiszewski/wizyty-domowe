package com.medical.wizytydomowe.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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

        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

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

        logoutButton.setOnClickListener {
            preferenceManager.clearAuthToken()
            preferenceManager.clearRole()

            Toast.makeText(context, "Wylogowano pomyślnie.", Toast.LENGTH_SHORT).show()

            (activity as? MainActivity)?.setMenuForUser(PreferenceManager(requireContext()))
            moveToLoginFragment()
        }

        val emailEditButton = view.findViewById<ImageButton>(R.id.editEmailButton)
        val phoneNumberEditButton = view.findViewById<ImageButton>(R.id.editPhoneNumberButton)
        val addressEditButton = view.findViewById<ImageButton>(R.id.editAddressButton)
        val passwordEditButton = view.findViewById<ImageButton>(R.id.editPasswordButton)

        emailEditButton.setOnClickListener {

        }

        phoneNumberEditButton.setOnClickListener {

        }

        addressEditButton.setOnClickListener {

        }

        passwordEditButton.setOnClickListener {

        }
    }

    private fun setPatientLayout(){
        view?.findViewById<LinearLayout>(R.id.hospitalLayout)?.visibility = View.GONE
        view?.findViewById<LinearLayout>(R.id.specializationLayout)?.visibility = View.GONE
        view?.findViewById<LinearLayout>(R.id.addreessLayout)?.visibility = View.VISIBLE
        view?.findViewById<LinearLayout>(R.id.streetLayout)?.visibility = View.VISIBLE
    }

    private fun setMedicalStaffLayout(){
        view?.findViewById<LinearLayout>(R.id.hospitalLayout)?.visibility = View.VISIBLE
        view?.findViewById<LinearLayout>(R.id.specializationLayout)?.visibility = View.VISIBLE
        view?.findViewById<LinearLayout>(R.id.addreessLayout)?.visibility = View.GONE
        view?.findViewById<LinearLayout>(R.id.streetLayout)?.visibility = View.GONE
    }

    private fun sendRequest(requestToken: String, userRole: String?){
        RetrofitInstance.apiService.getUserInfo(requestToken)
            .enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (userRole == "Patient"){
                            setPatientView(responseBody)
                        }
                        else{
                            setMedicalStaffView(responseBody)
                        }
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
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, loginFragment)
        transaction.addToBackStack(null)
        transaction.commit()
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
                val city = parts[0].trim()
                val postalCode = parts[1].trim()
                val street = parts[2].trim()
                view?.findViewById<TextView>(R.id.cityAndPostalCodeTextView)?.text = "${city}, ${postalCode}"
                view?.findViewById<TextView>(R.id.streetTextView)?.text = "ul. ${street}"
            }
        }
        else{
            view?.findViewById<TextView>(R.id.cityAndPostalCodeTextView)?.text = "None"
            view?.findViewById<TextView>(R.id.streetTextView)?.text = "None"
        }
    }

    private fun setPersonalData(userInfo: UserInfoResponse?){
        view?.findViewById<TextView>(R.id.personalDataTextView)?.text = "${userInfo?.firstName} ${userInfo?.lastName}"
        view?.findViewById<TextView>(R.id.emailTextView)?.text = "${userInfo?.email}"
        view?.findViewById<TextView>(R.id.phoneNumberTextView)?.text = "123-456-789"

        val dateOfBirth = convertToDateFormat(userInfo?.dateOfBirth)
        if (!dateOfBirth.isNullOrEmpty()) view?.findViewById<TextView>(R.id.dateOfBirthTextView)?.text = "${dateOfBirth}"
        else view?.findViewById<TextView>(R.id.dateOfBirthTextView)?.text = "None"

        view?.findViewById<TextView>(R.id.passwordTextView)?.text = "*******"
    }

    private fun setPatientView(userInfo: UserInfoResponse?){
        setPersonalData(userInfo)
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