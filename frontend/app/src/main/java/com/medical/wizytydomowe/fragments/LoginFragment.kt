package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.MainActivity
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import com.medical.wizytydomowe.api.login.LoginRequest
import com.medical.wizytydomowe.api.login.LoginResponse
import com.medical.wizytydomowe.api.registration.RegisterRequest
import com.medical.wizytydomowe.api.registration.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment(R.layout.login_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerTextView = view.findViewById<TextView>(R.id.tv_register)
        registerTextView.setOnClickListener {
            val registerFragment = RegisterFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, registerFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Przycisk logowania
        val loginButton = view.findViewById<Button>(R.id.btn_login)

        loginButton.setOnClickListener {
            var email = view.findViewById<EditText>(R.id.et_username).text.toString()
            var password = view.findViewById<EditText>(R.id.et_password).text.toString()

            // Placeholder
            if (!validateInputs(email, password)) {
                return@setOnClickListener
            } else {

                // Tworzenie obiektu żądania
                val loginRequest = LoginRequest(
                    email = email,
                    password = password
                )

                RetrofitInstance.apiService.login(loginRequest).enqueue(object :
                    Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()

                            Toast.makeText(context, "Logowanie przebiegło pomyślnie.", Toast.LENGTH_LONG).show()

                            val token = loginResponse?.token

                            val refresh_token = loginResponse?.refresh_token

                            val role = loginResponse?.role

                            if (token != null && refresh_token != null && role != null) {
                                val preferenceManager = PreferenceManager(requireContext())
                                preferenceManager.saveAuthToken(token)
                                preferenceManager.saveRefreshAuthToken(refresh_token)
                                preferenceManager.saveRole(role)
                            }
                            else{
                                Toast.makeText(context, "Wystąpił bład podczas logowania.", Toast.LENGTH_SHORT).show()
                                return
                            }

                            val activity = activity as? FragmentNavigation
                            (activity as? MainActivity)?.setMenuForUser(PreferenceManager(requireContext()))
                            activity?.navigateToFragment(ProfileFragment())


                        }
                        else {
                            //Debug text
                            Toast.makeText(context, "Logowanie nie powiodło się.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // Obsługa błędu połączenia
                        Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                Toast.makeText(context, "Pole 'Email' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(context, "Nieprawidłowy format email", Toast.LENGTH_SHORT).show()
                return false
            }
            password.isEmpty() -> {
                Toast.makeText(context, "Pole 'Hasło' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()

        val emailEditText = view?.findViewById<EditText>(R.id.et_username)
        val passwordEditText = view?.findViewById<EditText>(R.id.et_password)

        // Czyszczenie pól przy ponownym wejściu do fragmentu
        emailEditText?.text?.clear()
        passwordEditText?.text?.clear()
    }
}