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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddMedicalReportFragment : Fragment(R.layout.add_medical_report)  {

    private lateinit var preferenceManager: PreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFirstName: EditText = view.findViewById(R.id.etFirstName)
        val etLastName: EditText = view.findViewById(R.id.etLastName)
        val etAddress: EditText = view.findViewById(R.id.etAddress)
        val etDescription: EditText = view.findViewById(R.id.etDescription)
        val tvFirstName: TextView = view.findViewById(R.id.tvFirstName)
        val tvLastName: TextView = view.findViewById(R.id.tvLastName)
        val btnSubmit: Button = view.findViewById(R.id.btnSubmit)

        if(preferenceManager.isLoggedIn()){
            //TODO get user info
            etFirstName.visibility = View.GONE
            etLastName.visibility = View.GONE
            tvFirstName.text = "Imię: TEST"
            tvLastName.text = "Nazwisko: TESTOWY"
            tvFirstName.visibility = View.VISIBLE
            tvLastName.visibility = View.VISIBLE
        }
        else{
            etFirstName.visibility = View.VISIBLE
            etLastName.visibility = View.VISIBLE
            tvFirstName.visibility = View.GONE
            tvLastName.visibility = View.GONE
        }

        btnSubmit.setOnClickListener {
            val address = etAddress.text.toString()
            val description = etDescription.text.toString()
            val firstName: String
            val lastName: String
            if (preferenceManager.isLoggedIn()){
                firstName = tvFirstName.text.toString().split(" ").getOrNull(1) ?: ""
                lastName = tvLastName.text.toString().split(" ").getOrNull(1) ?: ""
            }
            else{
                firstName = etFirstName.text.toString()
                lastName = etLastName.text.toString()
            }
            //TODO send medical report to backend
            if (!validateInputs(firstName, lastName, address, description)) {
                return@setOnClickListener
            }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())

            val medicalReport = MedicalReport(null, "available", firstName, lastName, formattedDate, address, description)
            Toast.makeText(context, "Zgłoszenie zostało zarejestrowane pomyślnie.", Toast.LENGTH_SHORT).show()
            clearForm()
        }

    }

    private fun validateInputs(firstName: String, lastName: String, address: String,
                               description: String): Boolean {
        when {
            firstName.isEmpty() -> {
                Toast.makeText(context, "Pole 'Imię' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            lastName.isEmpty() -> {
                Toast.makeText(context, "Pole 'Nazwisko' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            address.isEmpty() -> {
                Toast.makeText(context, "Pole 'Adres' jest wymagane", Toast.LENGTH_SHORT).show()
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
        val etAddress = view?.findViewById<EditText>(R.id.etAddress)
        val etDescription = view?.findViewById<EditText>(R.id.etDescription)


        // Czyszczenie pól przy ponownym wejściu do fragmentu
        etFirstName?.text?.clear()
        etLastName?.text?.clear()
        etAddress?.text?.clear()
        etDescription?.text?.clear()
    }

    override fun onResume() {
        super.onResume()

        clearForm()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferenceManager = PreferenceManager(requireContext())
    }


}