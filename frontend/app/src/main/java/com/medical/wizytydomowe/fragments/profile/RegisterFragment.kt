package com.medical.wizytydomowe.fragments.profile

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import com.medical.wizytydomowe.api.registration.RegisterRequest
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

        val page1 = view.findViewById<ConstraintLayout>(R.id.form_container1)
        val page2 = view.findViewById<ConstraintLayout>(R.id.form_container2)
        val page3 = view.findViewById<ConstraintLayout>(R.id.form_container3)

        setPage(page1, page2, page3, pageNumber)

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
                setPage(page1, page2, page3, pageNumber)
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
                setPage(page1, page2, page3, pageNumber)
            }
        }

        buttonNextAtPrev2.setOnClickListener {
            pageNumber -= 1
            setPage(page1, page2, page3, pageNumber)
        }

        buttonNextAtPrev3.setOnClickListener {
            pageNumber -= 1
            setPage(page1, page2, page3, pageNumber)
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
                    moveToLoginFragment()
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

    private fun moveToLoginFragment(){
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

    private fun convertToDateFormat(dateString: String?): String? {
        try {
            if (!dateString.isNullOrEmpty()){
                val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                return date?.let { outputFormat.format(it) }
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    private fun setPage(page1 : ConstraintLayout, page2 : ConstraintLayout, page3 : ConstraintLayout,
                        pageNumber: Int){
        when{
            pageNumber == 1 -> {
                page1.visibility = View.VISIBLE
                page2.visibility = View.GONE
                page3.visibility = View.GONE
            }
            pageNumber == 2 -> {
                page1.visibility = View.GONE
                page2.visibility = View.VISIBLE
                page3.visibility = View.GONE
            }
            pageNumber == 3 -> {
                page1.visibility = View.GONE
                page2.visibility = View.GONE
                page3.visibility = View.VISIBLE
            }
        }
    }

    private fun validateFieldsPage1(firstName: String?, lastName: String?, email: String?,
                                    phoneNumber: String?, dateOfBirth: String?): Boolean{
        val firstNameLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutFirstName)
        val lastNameLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutLastName)
        val emailLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val phoneNumberLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPhoneNumber)
        val dateOfBirthLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutDateOfBirth)

        firstNameLayout?.error = null
        lastNameLayout?.error = null
        emailLayout?.error = null
        phoneNumberLayout?.error = null
        dateOfBirthLayout?.error = null

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

    private fun validateFieldsPage2(city: String?, street: String?, postalCode: String?,
                                    buildingNumber: String?): Boolean{
        val cityLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutCity)
        val streetLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutStreet)
        val postalCodeLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPostalCode)
        val buildingNumberLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutBuildingNumber)

        cityLayout?.error = null
        streetLayout?.error = null
        postalCodeLayout?.error = null
        buildingNumberLayout?.error = null

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

    private fun validateFieldsPage3(password: String?, passwordConfirmation : String?): Boolean{
        val passwordConfirmationLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPasswordConfirmation)
        val passwordLayout = view?.findViewById<TextInputLayout>(R.id.textInputLayoutPassword)

        passwordLayout?.error = null
        passwordConfirmationLayout?.error = null

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