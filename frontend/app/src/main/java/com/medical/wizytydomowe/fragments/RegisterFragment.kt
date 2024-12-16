package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import com.medical.wizytydomowe.api.registration.RegisterRequest
import com.medical.wizytydomowe.api.registration.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment(R.layout.register_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Przycisk rejestracji
        val registerButton = view.findViewById<Button>(R.id.btn_register)

        registerButton.setOnClickListener {
            val firstName = view.findViewById<EditText>(R.id.et_first_name).text.toString()
            val lastName = view.findViewById<EditText>(R.id.et_last_name).text.toString()
            var email = view.findViewById<EditText>(R.id.et_email).text.toString()
            val dateOfBirth = view.findViewById<EditText>(R.id.et_date_of_birth).text.toString()
            val phoneNumber = view.findViewById<EditText>(R.id.et_phone_number).text.toString()
            var password = view.findViewById<EditText>(R.id.et_password).text.toString()

            // Test
            password = "pistol"
            email = "eve.holt@reqres.in"



            // Placeholder
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                dateOfBirth.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Proszę wypełnić wszystkie pola", Toast.LENGTH_SHORT).show()
            } else {

                val registerRequest = RegisterRequest(email,password)

                RetrofitInstance.apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        if (response.isSuccessful) {
                            val registerResponse = response.body()

                            val token = registerResponse?.token

                            if (token != null) {
                                val preferenceManager = PreferenceManager(requireContext())
                                preferenceManager.saveAuthToken(token)

                                // Debug text
                                Toast.makeText(context, "Token: " + registerResponse?.token, Toast.LENGTH_SHORT).show()
                            }

                            val activity = activity as? FragmentNavigation
                            activity?.navigateToFragment(ProfileFragment())
                        }
                        else {
                            //Debug text
                            Toast.makeText(context, "Błąd rejestracji: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        // Obsługa błędu połączenia
                        Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

}