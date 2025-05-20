package com.medical.wizytydomowe.fragments.appointments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.authApi.AuthRetrofitInstance
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Nurse
import com.medical.wizytydomowe.api.users.User
import com.medical.wizytydomowe.api.users.UserAdapter
import com.medical.wizytydomowe.api.utils.convertToDateFormat
import com.medical.wizytydomowe.api.utils.validateDescription
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddAppointmentFragment : Fragment(R.layout.add_appointment_fragment)  {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter

    private var startDateAppointment: String? = null
    private var endDateAppointment: String? = null
    private var notes: String? = null
    private var durationTime: String? = null
    private var currentUserId: String? = null

    private lateinit var addAppointmentAloneView: MaterialCardView
    private lateinit var addAppointmentWithSomeoneView: MaterialCardView
    private lateinit var addOneAppointmentView: MaterialCardView
    private lateinit var addMonthAppointmentView: MaterialCardView
    private lateinit var addOneAppointmentFormView: MaterialCardView
    private lateinit var addMonthAppointmentFormView: MaterialCardView
    private lateinit var userRecyclerView: RecyclerView

    private lateinit var setAppointmentStartDateButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addAppointmentAloneView = view.findViewById(R.id.addAppointmentAloneView)
        addAppointmentWithSomeoneView = view.findViewById(R.id.addAppointmentWithSomeoneView)
        addOneAppointmentView = view.findViewById(R.id.addOneAppointmentView)
        addMonthAppointmentView = view.findViewById(R.id.addMonthAppointmentView)
        addOneAppointmentFormView = view.findViewById(R.id.addOneAppointmentFormView)
        addMonthAppointmentFormView = view.findViewById(R.id.addMonthAppointmentFormView)
        userRecyclerView = view.findViewById(R.id.userRecyclerView)

        preferenceManager = PreferenceManager(requireContext())

        setAppointmentStartDateButton = view.findViewById(R.id.setAppointmentStartDateButton)
        val buttonPrevAddOneAppointment = view.findViewById<Button>(R.id.buttonPrevAddOneAppointment)
        val buttonAddOneAppointment = view.findViewById<Button>(R.id.buttonAddOneAppointment)

        buttonPrevAddOneAppointment.setOnClickListener { goBackToAppointmentAloneLayout() }
        buttonAddOneAppointment.setOnClickListener {
            notes = view.findViewById<TextInputEditText>(R.id.textInputEditTextNotes).text.toString()
            durationTime = view.findViewById<TextInputEditText>(R.id.textInputEditTextDuration).text.toString()

            if (!startDateAppointment.isNullOrEmpty()) addOneAppointment(notes, durationTime)
            else Toast.makeText(requireContext(), "Musisz dodać datę wizyty.", Toast.LENGTH_SHORT).show()
        }
        setAppointmentStartDateButton.setOnClickListener { setAppointmentDate() }

        addAppointmentAloneView.setOnClickListener { setAppointmentAloneLayout() }
        addAppointmentWithSomeoneView.setOnClickListener {

        }
        addMonthAppointmentView.setOnClickListener { setAddMonthAppointmentLayout() }
        addOneAppointmentView.setOnClickListener { setAddOneAppointmentLayout() }

        getCurrentUserId("Bearer " + preferenceManager.getAuthToken())
        setMainMenu()
    }

    private fun setMainMenu(){
        addAppointmentAloneView.visibility = View.VISIBLE
        addAppointmentWithSomeoneView.visibility = View.VISIBLE
        addMonthAppointmentView.visibility = View.GONE
        addOneAppointmentView.visibility = View.GONE
        addOneAppointmentFormView.visibility = View.GONE
        addMonthAppointmentFormView.visibility = View.GONE
        userRecyclerView.visibility = View.GONE
    }

    private fun setAppointmentAloneLayout(){
        addAppointmentAloneView.visibility = View.GONE
        addAppointmentWithSomeoneView.visibility = View.GONE
        addMonthAppointmentView.visibility = View.VISIBLE
        addOneAppointmentView.visibility = View.VISIBLE
    }

    private fun goBackToAppointmentAloneLayout(){
        addMonthAppointmentView.visibility = View.VISIBLE
        addOneAppointmentView.visibility = View.VISIBLE
        addOneAppointmentFormView.visibility = View.GONE
    }

    private fun setAppointmentWithSomeoneLayout(){

    }

    private fun setAddOneAppointmentLayout(){
        addMonthAppointmentView.visibility = View.GONE
        addOneAppointmentView.visibility = View.GONE
        addOneAppointmentFormView.visibility = View.VISIBLE
    }

    private fun setAddMonthAppointmentLayout(){
        addMonthAppointmentView.visibility = View.GONE
        addOneAppointmentView.visibility = View.GONE
        addMonthAppointmentFormView.visibility = View.VISIBLE
    }

    private fun setRecyclerView(users: List<User>){
        recyclerView = userRecyclerView

        userAdapter = UserAdapter(users) { user ->

        }

        recyclerView.adapter = userAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun setAppointmentDate(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Wybierz termin rozpoczęcia wizyty")
                .build()

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Date(selectedDate)
            val formattedDate = dateFormat.format(date)

            startDateAppointment = formattedDate
            setAppointmentTime()
        }

        datePicker.show((context as AppCompatActivity).supportFragmentManager, "DATE_PICKER")
    }

    private fun setAppointmentTime(){
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Wybierz godzinę rozpoczęcia wizyty")
                .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute

            val formattedTime = String.format("%02d:%02d:%02d", hour, minute, 0)
            val date = startDateAppointment
            val time = formattedTime
            startDateAppointment += "T${formattedTime}"
            setAppointmentStartDateButton.text = "${convertToDateFormat(date, "yyyy-MM-dd", "dd-MM-yyyy")} ${time}"
        }

        timePicker.show((context as AppCompatActivity).supportFragmentManager, "TIME_PICKER")
    }

    private fun addOneAppointment(notes: String?, durationTime: String?) {
        val textInputLayoutDuration = view?.findViewById<TextInputLayout>(R.id.textInputLayoutDuration)
        val textInputLayoutNotes = view?.findViewById<TextInputLayout>(R.id.textInputLayoutNotes)

        textInputLayoutDuration?.error = null

        if (durationTime.isNullOrEmpty()) textInputLayoutDuration?.error = "Pole 'Czas trwania wizyty' nie może być puste"
        else {
            if (validateDescription(notes, textInputLayoutNotes)){
                countEndTimeAppointment()
                if (endDateAppointment != null && currentUserId != null){
                    var doctorId: String? = null
                    var nurseId: String? = null
                    var appointment: Appointment? = null
                    if (preferenceManager.getRole() == "Doctor"){
                        doctorId = currentUserId
                        appointment = Appointment(null, "AVAILABLE", startDateAppointment, endDateAppointment,
                            Doctor(doctorId, null, null, null, null),
                            null, null, null, notes)
                    }
                    if (preferenceManager.getRole() == "Nurse"){
                        nurseId = currentUserId
                        appointment = Appointment(null, "AVAILABLE", startDateAppointment, endDateAppointment,
                            null, Nurse(nurseId, null, null, null),
                            null, null, notes)
                    }
                    navigateToAppointmentDetails(appointment, "singleAppointment")
                }
                else Toast.makeText(requireContext(), "Wystąpił błąd podczas dodawania wizyty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun countEndTimeAppointment(){
        val durationTimeConverted = "00:${durationTime}:00"
        try{
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val startDate: Date = dateFormat.parse(startDateAppointment.toString())!!

            val parts = durationTimeConverted.split(":").map { it.toInt() }
            val hours = parts[0]
            val minutes = parts[1]
            val seconds = parts[2]

            val calendar = Calendar.getInstance()
            calendar.time = startDate

            calendar.add(Calendar.HOUR_OF_DAY, hours)
            calendar.add(Calendar.MINUTE, minutes)
            calendar.add(Calendar.SECOND, seconds)

            endDateAppointment = dateFormat.format(calendar.time)
        }
        catch (e: Exception){
            endDateAppointment = null
        }

    }

    private fun navigateToAppointmentDetails(appointment: Appointment?, addNewAppointmentFlag: String){
        val bundle = Bundle().apply {
            putSerializable("appointment", appointment)
            putSerializable("addNewAppointmentFlag", addNewAppointmentFlag)
        }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AppointmentDetailsFragment().apply { arguments = bundle })
    }

    private fun getCurrentUserId(requestToken: String){
        AuthRetrofitInstance.authApiService.getUserInfo(requestToken)
            .enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        currentUserId = responseBody?.id
                    } else {
                        Log.e("API", "Błąd: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    Log.e("API", "Niepowodzenie: ${t.message}")
                }
            })
    }

}