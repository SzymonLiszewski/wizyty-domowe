package com.medical.wizytydomowe.fragments.profile

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import com.medical.wizytydomowe.api.registration.RegisterRequest
import com.medical.wizytydomowe.api.utils.*
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment(R.layout.register_fragment) {

    private var pageNumber : Int = 1

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
    private var password: String? = null
    private var passwordConfirmation: String? = null
    private var address: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formView1 = view.findViewById<MaterialCardView>(R.id.formView1)
        val formView2 = view.findViewById<MaterialCardView>(R.id.formView2)
        val formView3 = view.findViewById<MaterialCardView>(R.id.formView3)

        setPage(formView1, formView2, formView3, pageNumber)

        val buttonNextAtPage1 = view.findViewById<Button>(R.id.btn_next1)
        val buttonNextAtPage2 = view.findViewById<Button>(R.id.btn_next2)
        val buttonNextAtPrev2 = view.findViewById<Button>(R.id.btn_prev2)
        val buttonNextAtPrev3 = view.findViewById<Button>(R.id.btn_prev3)
        val buttonRegister = view.findViewById<Button>(R.id.btn_register)

        buttonNextAtPage1.setOnClickListener {
            firstName = view.findViewById<TextInputEditText>(R.id.textInputEditTextFirstName).text.toString()
            lastName = view.findViewById<TextInputEditText>(R.id.textInputEditTextLastName).text.toString()
            email = view.findViewById<TextInputEditText>(R.id.textInputEditTextEmail).text.toString()
            phoneNumber = view.findViewById<TextInputEditText>(R.id.textInputEditTextPhoneNumber).text.toString()
            dateOfBirth = view.findViewById<TextInputEditText>(R.id.textInputEditTextDateOfBirth).text.toString()

            if (validateFieldsPage1(firstName, lastName, email, phoneNumber, dateOfBirth)){
                pageNumber += 1
                setPage(formView1, formView2, formView3, pageNumber)
            }
        }

        buttonNextAtPage2.setOnClickListener {
            city = view.findViewById<TextInputEditText>(R.id.textInputEditTextCity).text.toString()
            street = view.findViewById<TextInputEditText>(R.id.textInputEditTextStreet).text.toString()
            postalCode = view.findViewById<TextInputEditText>(R.id.textInputEditTextPostalCode).text.toString()
            apartmentNumber = view.findViewById<TextInputEditText>(R.id.textInputEditTextApartmentNumber).text.toString()
            buildingNumber = view.findViewById<TextInputEditText>(R.id.textInputEditTextBuildingNumber).text.toString()

            if (validateFieldsPage2(city, street, postalCode, buildingNumber)){
                pageNumber += 1
                setPage(formView1, formView2, formView3, pageNumber)
            }
        }

        buttonNextAtPrev2.setOnClickListener {
            pageNumber -= 1
            setPage(formView1, formView2, formView3, pageNumber)
        }

        buttonNextAtPrev3.setOnClickListener {
            pageNumber -= 1
            setPage(formView1, formView2, formView3, pageNumber)
        }

        buttonRegister.setOnClickListener {
            passwordConfirmation = view.findViewById<TextInputEditText>(R.id.textInputEditTextPasswordConfirmation).text.toString()
            password = view.findViewById<TextInputEditText>(R.id.textInputEditTextPassword).text.toString()

            if (validateFieldsPage3(password, passwordConfirmation)){
                val dateOfBirthRequest = convertToDateFormat(dateOfBirth)
                if (dateOfBirth == null) {
                    Toast.makeText(context, "Wystąpił błąd podczas rejestracji.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (apartmentNumber.isNullOrEmpty()) address = "${city}, ${postalCode}, ${street} ${buildingNumber}"
                else address = "${city}, ${postalCode}, ${street} ${buildingNumber}/${apartmentNumber}"

                val registerRequest = RegisterRequest(
                    firstName = firstName.toString(),
                    lastName = lastName.toString(),
                    email = email.toString(),
                    password = password.toString(),
                    phoneNumber = phoneNumber.toString(),
                    dateOfBirth = dateOfBirthRequest.toString(),
                    address = address.toString()
                )
                sendRegisterRequest(registerRequest)
            }
        }
    }

    private fun sendRegisterRequest(registerRequest: RegisterRequest){
        RetrofitInstance.apiService.register(registerRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Rejestracja przebiegła pomyślnie.", Toast.LENGTH_LONG).show()
                    navigateToLoginFragment()
                }
                else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Podczas rejestracji wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToLoginFragment(){
        val loginFragment = LoginFragment()

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(loginFragment)
    }

    private fun isValidDate(dateString: String?): Boolean {
        try {
            if (!dateString.isNullOrEmpty()){
                val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                format.isLenient = false
                format.parse(dateString)
                return true
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    private fun setPage(formView1 : MaterialCardView, formView2 : MaterialCardView,
                        formView3 : MaterialCardView, pageNumber: Int){
        when{
            pageNumber == 1 -> {
                formView1.visibility = View.VISIBLE
                formView2.visibility = View.GONE
                formView3.visibility = View.GONE
            }
            pageNumber == 2 -> {
                formView1.visibility = View.GONE
                formView2.visibility = View.VISIBLE
                formView3.visibility = View.GONE
            }
            pageNumber == 3 -> {
                formView1.visibility = View.GONE
                formView2.visibility = View.GONE
                formView3.visibility = View.VISIBLE
            }
        }
    }

    private fun resetErrorsPage1(firstNameLayout: TextInputLayout?, lastNameLayout: TextInputLayout?,
                                 emailLayout: TextInputLayout?, phoneNumberLayout: TextInputLayout?,
                                 dateOfBirthLayout: TextInputLayout?){
        firstNameLayout?.error = null
        lastNameLayout?.error = null
        emailLayout?.error = null
        phoneNumberLayout?.error = null
        dateOfBirthLayout?.error = null
    }

    private fun validateFieldsPage1(firstName: String?, lastName: String?, email: String?,
                                    phoneNumber: String?, dateOfBirth: String?): Boolean{
        val firstNameLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutFirstName)
        val lastNameLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutLastName)
        val emailLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val phoneNumberLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPhoneNumber)
        val dateOfBirthLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutDateOfBirth)

        resetErrorsPage1(firstNameLayout, lastNameLayout, emailLayout, phoneNumberLayout, dateOfBirthLayout)

        when{
            firstName.isNullOrEmpty() -> {
                firstNameLayout?.error = "Pole 'Imię' jest wymagane"
                return false
            }
            lastName.isNullOrEmpty() -> {
                lastNameLayout?.error = "Pole 'Nazwisko' jest wymagane"
                return false
            }
            email.isNullOrEmpty() -> {
                emailLayout?.error = "Pole 'E-mail' jest wymagane"
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailLayout?.error = "Nieprawidłowy format pola 'E-mail'"
                return false
            }
            phoneNumber.isNullOrEmpty() -> {
                phoneNumberLayout?.error = "Pole 'Numer telefonu' jest wymagane"
                return false
            }
            !phoneNumber.matches("^\\d{9}\$".toRegex()) -> {
                phoneNumberLayout?.error = "Nieprawidłowy format pola 'Numer telefonu'"
                return false
            }
            dateOfBirth.isNullOrEmpty() -> {
                dateOfBirthLayout?.error = "Pole 'Data urodzenia' jest wymagane"
                return false
            }
            !dateOfBirth.matches("^\\d{2}-\\d{2}-\\d{4}\$".toRegex()) -> {
                dateOfBirthLayout?.error = "Nieprawidłowy format pola 'Data urodzenia'"
                return false
            }
            !isValidDate(dateOfBirth) -> {
                dateOfBirthLayout?.error = "Nieprawidłowy format pola 'Data urodzenia'"
                return false
            }
        }
        return true
    }

    private fun resetErrorsPage2(cityLayout: TextInputLayout?, streetLayout: TextInputLayout?,
                                 postalCodeLayout: TextInputLayout?, buildingNumberLayout: TextInputLayout?){
        cityLayout?.error = null
        streetLayout?.error = null
        postalCodeLayout?.error = null
        buildingNumberLayout?.error = null
    }

    private fun validateFieldsPage2(city: String?, street: String?, postalCode: String?,
                                    buildingNumber: String?): Boolean{
        val cityLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutCity)
        val streetLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutStreet)
        val postalCodeLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPostalCode)
        val buildingNumberLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutBuildingNumber)

        resetErrorsPage2(cityLayout, streetLayout, postalCodeLayout, buildingNumberLayout)

        when{
            city.isNullOrEmpty() -> {
                cityLayout?.error = "Pole 'Miasto' jest wymagane"
                return false
            }
            street.isNullOrEmpty() -> {
                streetLayout?.error = "Pole 'Ulica' jest wymagane"
                return false
            }
            buildingNumber.isNullOrEmpty() -> {
                buildingNumberLayout?.error = "Pole 'Numer budynku' jest wymagane"
                return false
            }
            postalCode.isNullOrEmpty() -> {
                postalCodeLayout?.error = "Pole 'Kod pocztowy' jest wymagane"
                return false
            }
            !postalCode.matches("^\\d{2}-\\d{3}\$".toRegex()) -> {
                postalCodeLayout?.error = "Nieprawidłowy format pola 'Kod pocztowy'"
                return false
            }
        }
        return true
    }

    private fun resetErrorsOnPage3(passwordLayout: TextInputLayout?, passwordConfirmationLayout: TextInputLayout?){
        passwordLayout?.error = null
        passwordConfirmationLayout?.error = null
    }

    private fun validateFieldsPage3(password: String?, passwordConfirmation : String?): Boolean{
        val passwordConfirmationLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPasswordConfirmation)
        val passwordLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPassword)

        resetErrorsOnPage3(passwordLayout, passwordConfirmationLayout)

        when{
            password.isNullOrEmpty() -> {
                passwordLayout?.error = "Pole 'Hasło' jest wymagane"
                return false
            }
            password.length < 6 -> {
                passwordLayout?.error = "Pole 'Hasło' powinno zawierać min. 6 znaków"
                return false
            }
            passwordConfirmation.isNullOrEmpty() -> {
                passwordConfirmationLayout?.error = "Pole 'Potwierdzenie hasła' jest wymagane"
                return false
            }
            !password.equals(passwordConfirmation) -> {
                passwordConfirmationLayout?.error = "Wprowadzone hasła nie są jednakowe"
                return false
            }
        }
        return true
    }

}