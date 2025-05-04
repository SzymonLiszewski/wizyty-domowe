package com.medical.wizytydomowe.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.users.Doctor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddVisitFragment : Fragment(R.layout.add_visit_fragment)  {

    private lateinit var preferenceManager: PreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFirstName: EditText = view.findViewById(R.id.etFirstName)
        val etLastName: EditText = view.findViewById(R.id.etLastName)
        val btnSubmit: Button = view.findViewById(R.id.btnSubmit)
        val btnSelectDate: Button = view.findViewById(R.id.btnSelectDate)
        val btnSelectStartTime: Button = view.findViewById(R.id.btnSelectStartTime);
        val btnSelectEndTime: Button = view.findViewById(R.id.btnSelectEndTime);
        val tvSelectedDate: TextView = view.findViewById(R.id.tvSelectedDate);
        val tvSelectedStartTime: TextView = view.findViewById(R.id.tvSelectedStartTime);
        val tvSelectedEndTime: TextView = view.findViewById(R.id.tvSelectedEndTime);

        tvSelectedDate.text = "Wybrana data: -"
        tvSelectedStartTime.text = "Wybrana godzina: -"
        tvSelectedEndTime.text = "Wybrana godzina: -"

        btnSelectDate.setOnClickListener {
            showDatePicker { selectedDate ->
                tvSelectedDate.text = "Wybrana data: $selectedDate"
            }
        }

        btnSelectStartTime.setOnClickListener {
            showTimePicker { selectedTime ->
                tvSelectedStartTime.text = "Wybrana godzina: $selectedTime"
            }
        }

        btnSelectEndTime.setOnClickListener {
            showTimePicker { selectedTime ->
                tvSelectedEndTime.text = "Wybrana godzina: $selectedTime"
            }
        }

        btnSubmit.setOnClickListener {
            val firstName: String
            val lastName: String
            val selectedDate = tvSelectedDate.text.takeLast(10).toString()
            val selectedStartTime = tvSelectedStartTime.text.takeLast(5).toString()
            val selectedEndTime = tvSelectedEndTime.text.takeLast(5).toString()
            firstName = etFirstName.text.toString()
            lastName = etLastName.text.toString()


            if (!validateInputs(selectedDate, selectedStartTime, selectedEndTime)) {
                return@setOnClickListener
            }

            //TODO get user info - doctor or nurse who added a prescription
            val doctor = Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku")

            //TODO check if doctor/nurse enetered by the user exists -> get his user info or backend will check it
            // TODO add nurse to appointments ????
            //if (firstName.isNotEmpty() && lastName.isNotEmpty()){
                //TODO check if doctor/nurse exist; add to the appointment

            //}
            val appointment = Appointment(null, "available", "$selectedDate $selectedStartTime", "$selectedDate $selectedEndTime", doctor, null, null, null, null)

            //TODO send appointment to backend
            Toast.makeText(context, "Wizyta jest już widoczna dla pacjentów jako dostępna.", Toast.LENGTH_SHORT).show()
            clearForm()
        }

    }

    private fun validateInputs(selectedDate: String, selectedStartTime: String,
                               selectedEndTime: String): Boolean {
        when {
            selectedDate.isEmpty() || selectedDate.last() == '-' -> {
                Toast.makeText(context, "Pole 'Data' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            selectedStartTime.isEmpty() || selectedStartTime.last() == '-' -> {
                Toast.makeText(context, "Pole 'Godzina rozpoczęcia' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            selectedEndTime.isEmpty() || selectedEndTime.last() == '-' -> {
                Toast.makeText(context, "Pole 'Godzina Zakończenia' jest wymagane", Toast.LENGTH_SHORT).show()
                return false
            }
            checkIfStartTimeIsBeforeEndTime(selectedStartTime, selectedEndTime) -> {
                Toast.makeText(requireContext(), "Godzina zakończenia musi być późniejsza niż godzina rozpoczęcia", Toast.LENGTH_SHORT).show()
                return false
            }
            checkIfDateIsInTheFuture(selectedDate) -> {
                Toast.makeText(requireContext(), "Data nie może być w przeszłości", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun clearForm(){
        val etFirstName = view?.findViewById<EditText>(R.id.etFirstName)
        val etLastName= view?.findViewById<EditText>(R.id.etLastName)
        val tvSelectedDate = view?.findViewById<TextView>(R.id.tvSelectedDate);
        val tvSelectedStartTime = view?.findViewById<TextView>(R.id.tvSelectedStartTime);
        val tvSelectedEndTime = view?.findViewById<TextView>(R.id.tvSelectedEndTime);

        // Czyszczenie pól przy ponownym wejściu do fragmentu
        etFirstName?.text?.clear()
        etLastName?.text?.clear()

        tvSelectedDate?.text = "Wybrana data: -"
        tvSelectedStartTime?.text = "Wybrana godzina: -"
        tvSelectedEndTime?.text = "Wybrana godzina: -"
    }

    override fun onResume() {
        super.onResume()

        clearForm()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferenceManager = PreferenceManager(requireContext())
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1, // Month is 0-based
                    selectedDay
                )
                onDateSelected(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(formattedTime)
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun checkIfStartTimeIsBeforeEndTime(selectedStartTime: String, selectedEndTime: String) : Boolean{
        val selectedStartParts = selectedStartTime.split(":")
        val selectedEndParts = selectedEndTime.split(":")

        val startHour = selectedStartParts[0].toInt()
        val startMinute = selectedStartParts[1].toInt()

        val endHour = selectedEndParts[0].toInt()
        val endMinute = selectedEndParts[1].toInt()

        if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
            return true
        }
        return false
    }

    private fun checkIfDateIsInTheFuture(selectedDate: String) : Boolean{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val selectedDateObj = dateFormat.parse(selectedDate)
        val currentDateObj = dateFormat.parse(currentDate)

        if (selectedDateObj.before(currentDateObj)) {
            return true
        }
        return false
    }

}