package com.medical.wizytydomowe.fragments.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.MainActivity
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import com.medical.wizytydomowe.api.login.LoginRequest
import com.medical.wizytydomowe.api.login.LoginResponse
import com.medical.wizytydomowe.api.utils.validateLoginInputs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment(R.layout.login_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerTextView = view.findViewById<TextView>(R.id.tv_register)
        val loginButton = view.findViewById<Button>(R.id.btn_login)

        registerTextView.setOnClickListener {
            navigateToRegisterFragment()
        }

        loginButton.setOnClickListener {
            val email = view.findViewById<TextInputEditText>(R.id.textInputEditTextEmail).text.toString()
            val password = view.findViewById<TextInputEditText>(R.id.textInputEditTextPassword).text.toString()

            if (!validateInputs(email, password)) {
                return@setOnClickListener
            } else {
                val loginRequest = LoginRequest(
                    email = email,
                    password = password
                )
                sendLoginRequest(loginRequest)
            }
        }
    }

    private fun sendLoginRequest(loginRequest: LoginRequest){
        RetrofitInstance.apiService.login(loginRequest).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    Toast.makeText(context, "Logowanie przebiegło pomyślnie.", Toast.LENGTH_LONG).show()

                    if (setTokenAndRole(loginResponse)) navigateToProfileFragment()
                    else Toast.makeText(context, "Wystąpił bład podczas logowania.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(context, "Logowanie nie powiodło się.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setTokenAndRole(loginResponse: LoginResponse?) : Boolean{
        val token = loginResponse?.token
        val refresh_token = loginResponse?.refresh_token
        val role = loginResponse?.role

        if (token != null && refresh_token != null && role != null) {
            val preferenceManager = PreferenceManager(requireContext())
            preferenceManager.saveAuthToken(token)
            preferenceManager.saveRefreshAuthToken(refresh_token)
            preferenceManager.saveRole(role)
            return true
        }
        return false
    }

    private fun validateInputs(email: String, password: String): Boolean {
        val emailLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val passwordLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPassword)

        if (!validateLoginInputs(email, password, emailLayout, passwordLayout)) return false
        return true
    }

    private fun navigateToRegisterFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(RegisterFragment())
    }

    private fun navigateToProfileFragment(){
        val activity = activity as? FragmentNavigation
        (activity as? MainActivity)?.setMenuForUser(PreferenceManager(requireContext()))
        activity?.navigateToFragment(ProfileFragment())
    }
}