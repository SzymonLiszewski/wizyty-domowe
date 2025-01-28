package com.medical.wizytydomowe.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import com.medical.wizytydomowe.api.registration.RegisterRequest
import com.medical.wizytydomowe.api.registration.RegisterResponse
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class RegisterFragment : Fragment(R.layout.register_fragment) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Przycisk rejestracji
        val registerButton = view.findViewById<Button>(R.id.btn_register)

        registerButton.setOnClickListener {
            val firstName = view.findViewById<EditText>(R.id.et_first_name).text.toString()
            val lastName = view.findViewById<EditText>(R.id.et_last_name).text.toString()
            var email = view.findViewById<EditText>(R.id.et_email).text.toString()
            val dateOfBirthInput = view.findViewById<EditText>(R.id.et_date_of_birth).text.toString()
            val phoneNumber = view.findViewById<EditText>(R.id.et_phone_number).text.toString()
            var password = view.findViewById<EditText>(R.id.et_password).text.toString()

            // Placeholder
            if (!validateInputs(firstName, lastName, email, dateOfBirthInput, phoneNumber, password)) {
                return@setOnClickListener
            } else {

                val dateOfBirth = convertToDateFormat(dateOfBirthInput)
                if (dateOfBirth == null) {
                    Toast.makeText(context, "Nieprawidłowy format daty. Użyj formatu: dd-MM-yyyy.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Tworzenie obiektu żądania
                val registerRequest = RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    phoneNumber = phoneNumber,
                    dateOfBirth = dateOfBirth
                )

                RetrofitInstance.apiService.register(registerRequest).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string()

                            try {
                                val jsonResponse = JSONObject(responseBody)
                                val registerResponse = Gson().fromJson(jsonResponse.toString(), RegisterResponse::class.java)

                                Toast.makeText(context, registerResponse.response ?: "Rejestracja przebiegła pomyślnie.", Toast.LENGTH_LONG).show()

                            } catch (e: JSONException) {
                                Toast.makeText(context, responseBody, Toast.LENGTH_LONG).show()
                            }

                            val loginFragment = LoginFragment()
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, loginFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }
                        else {
                            val errorMessage = response.errorBody()?.string()
                            Toast.makeText(context, "$errorMessage.", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        // Obsługa błędu połączenia
                        Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun validateInputs(firstName: String, lastName: String, email: String,
        dateOfBirthInput: String, phoneNumber: String, password: String): Boolean {
        when {
            firstName.isEmpty() -> {
                Toast.makeText(context, "Pole 'Imię' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            lastName.isEmpty() -> {
                Toast.makeText(context, "Pole 'Nazwisko' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            email.isEmpty() -> {
                Toast.makeText(context, "Pole 'Email' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(context, "Nieprawidłowy format email", Toast.LENGTH_SHORT).show()
                return false
            }
            dateOfBirthInput.isEmpty() -> {
                Toast.makeText(context, "Pole 'Data urodzenia' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            phoneNumber.isEmpty() -> {
                Toast.makeText(context, "Pole 'Numer telefonu' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            !phoneNumber.matches(Regex("[0-9]{3}[0-9]{3}[0-9]{3}\$")) -> {
                Toast.makeText(context, "Nieprawidłowy numer telefonu", Toast.LENGTH_SHORT).show()
                return false
            }
            password.isEmpty() -> {
                Toast.makeText(context, "Pole 'Hasło' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            password.length < 6 -> {
                Toast.makeText(context, "Hasło musi mieć co najmniej 6 znaków", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }


    fun convertToDateFormat(dateString: String): String? {
        return try {
            val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())  // Oczekiwany format użytkownika (np. 15-12-2024)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Docelowy format (rrrr-mm-dd)
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            null // W przypadku błędu w konwersji zwróci null
        }
    }

    override fun onResume() {
        super.onResume()

        val firstNameEditText = view?.findViewById<EditText>(R.id.et_first_name)
        val lastNameEditText = view?.findViewById<EditText>(R.id.et_last_name)
        val emailEditText = view?.findViewById<EditText>(R.id.et_email)
        val dateOfBirthEditText = view?.findViewById<EditText>(R.id.et_date_of_birth)
        val phoneNumberEditText = view?.findViewById<EditText>(R.id.et_phone_number)
        val passwordEditText = view?.findViewById<EditText>(R.id.et_password)

        // Czyszczenie pól przy każdym ponownym wejściu do fragmentu
        firstNameEditText?.text?.clear()
        lastNameEditText?.text?.clear()
        emailEditText?.text?.clear()
        dateOfBirthEditText?.text?.clear()
        phoneNumberEditText?.text?.clear()
        passwordEditText?.text?.clear()
    }
}