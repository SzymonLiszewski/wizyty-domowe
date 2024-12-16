package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.R

class RegisterFragment : Fragment(R.layout.register_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Przycisk rejestracji
        val registerButton = view.findViewById<Button>(R.id.btn_register)

        registerButton.setOnClickListener {
            val firstName = view.findViewById<EditText>(R.id.et_first_name).text.toString()
            val lastName = view.findViewById<EditText>(R.id.et_last_name).text.toString()
            val email = view.findViewById<EditText>(R.id.et_email).text.toString()
            val dateOfBirth = view.findViewById<EditText>(R.id.et_date_of_birth).text.toString()
            val phoneNumber = view.findViewById<EditText>(R.id.et_phone_number).text.toString()
            val password = view.findViewById<EditText>(R.id.et_password).text.toString()

            // Placeholder
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                dateOfBirth.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Proszę wypełnić wszystkie pola", Toast.LENGTH_SHORT).show()
            } else {
                // Obsłuż rejestrację Placeholder
                Toast.makeText(context, "Rejestracja zakończona sukcesem!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}