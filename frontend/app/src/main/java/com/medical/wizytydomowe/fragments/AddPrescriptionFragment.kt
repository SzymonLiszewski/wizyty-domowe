package com.medical.wizytydomowe.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.medicalReports.MedicalReport
import com.medical.wizytydomowe.api.prescriptions.Prescription
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Patient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPrescriptionFragment : Fragment(R.layout.add_prescription_fragment)  {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFirstName: EditText = view.findViewById(R.id.etFirstName)
        val etLastName: EditText = view.findViewById(R.id.etLastName)
        val etDescription: EditText = view.findViewById(R.id.etDescription)
        val btnSubmit: Button = view.findViewById(R.id.btnSubmit)


        btnSubmit.setOnClickListener {
            val description = etDescription.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()


            if (!validateInputs(firstName, lastName, description)) {
                return@setOnClickListener
            }

            //TODO get user info - doctor who added a prescription
            //TODO check if patient exist -> get his user info or backend will check it
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())

            val prescription = Prescription(null, formattedDate, Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"), Patient("1", firstName, lastName, "a", "a"), description)

            //TODO send prescription to backend
            Toast.makeText(context, "$formattedDate Recepta została dodana pomyślnie.", Toast.LENGTH_SHORT).show()
            clearForm()
        }

    }

    private fun validateInputs(firstName: String, lastName: String, description: String): Boolean {
        when {
            firstName.isEmpty() -> {
                Toast.makeText(context, "Pole 'Imię' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            lastName.isEmpty() -> {
                Toast.makeText(context, "Pole 'Nazwisko' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            description.isEmpty() -> {
                Toast.makeText(context, "Pole 'Opis' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun clearForm(){
        val etFirstName = view?.findViewById<EditText>(R.id.etFirstName)
        val etLastName= view?.findViewById<EditText>(R.id.etLastName)
        val etDescription = view?.findViewById<EditText>(R.id.etDescription)


        // Czyszczenie pól przy ponownym wejściu do fragmentu
        etFirstName?.text?.clear()
        etLastName?.text?.clear()
        etDescription?.text?.clear()
    }

    override fun onResume() {
        super.onResume()

        clearForm()
    }

}