package com.medical.wizytydomowe.fragments.editProfile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import com.medical.wizytydomowe.api.userInfo.EditPasswordRequest
import com.medical.wizytydomowe.api.utils.validateNewPassword
import com.medical.wizytydomowe.api.utils.validateOldPassword
import com.medical.wizytydomowe.fragments.profile.ProfileFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPasswordFragment: Fragment(R.layout.edit_password_fragment) {

    private lateinit var editPasswordFormView: MaterialCardView
    private var email: String? = null
    private var oldPassword: String? = null
    private var newPassword: String? = null
    private var newPasswordConfirmation: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = arguments?.getSerializable("userEmail") as String?
        editPasswordFormView = view.findViewById(R.id.editPasswordFormView)

        val buttonPrev = view.findViewById<Button>(R.id.buttonPrev)
        val buttonResetPassword = view.findViewById<Button>(R.id.buttonResetPassword)

        buttonPrev.setOnClickListener { navigateToProfileFragment() }

        buttonResetPassword.setOnClickListener { editPassword() }
    }

    private fun editPassword(){
        oldPassword = view?.findViewById<TextInputEditText>(R.id.textInputEditOldTextPassword)?.text.toString()
        newPassword = view?.findViewById<TextInputEditText>(R.id.textInputEditTextNewPassword)?.text.toString()
        newPasswordConfirmation = view?.findViewById<TextInputEditText>(R.id.textInputEditTextNewPasswordConfirmation)?.text.toString()

        if (validateInput(oldPassword, newPassword, newPasswordConfirmation)){
            val editPasswordRequest = EditPasswordRequest(email, oldPassword, newPassword)
            sendEditPasswordRequest(editPasswordRequest)
        }
    }

    private fun sendEditPasswordRequest(editPasswordRequest: EditPasswordRequest){
        RetrofitInstance.apiService.editPassword(editPasswordRequest).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Hasło zostało zmienione", Toast.LENGTH_LONG).show()
                    navigateToProfileFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Zmiana hasła nie powiodła się: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun validateInput(oldPassword: String?, newPassword: String?,
                              newPasswordConfirmation: String?): Boolean{
        val oldPasswordLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutOldPassword)
        val newPasswordLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutNewPassword)
        val newPasswordConfirmationLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutNewPasswordConfirmation)

        if (!validateOldPassword(oldPassword, oldPasswordLayout)) return false
        if (!validateNewPassword(newPassword, newPasswordConfirmation,
                newPasswordLayout, newPasswordConfirmationLayout)) return false
        return true
    }

    private fun navigateToProfileFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(ProfileFragment())
    }
}