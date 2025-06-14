package com.medical.wizytydomowe.fragments.appointments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.medical.wizytydomowe.api.appointmentApi.AppointmentRetrofitInstance
import com.medical.wizytydomowe.api.appointments.AddAppointmentCalendarRequest
import com.medical.wizytydomowe.api.appointments.Appointment
import com.medical.wizytydomowe.api.authApi.AuthRetrofitInstance
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Nurse
import com.medical.wizytydomowe.api.users.User
import com.medical.wizytydomowe.api.users.UserAdapter
import com.medical.wizytydomowe.api.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddAppointmentFragment : Fragment(R.layout.add_appointment_fragment)  {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var users: List<User>
    private var coworker: User? = null

    private var startDateAppointment: String? = null
    private var endDateAppointment: String? = null
    private var startTimeAppointment: String? = null
    private var notes: String? = null
    private var durationTime: String? = null
    private var currentUserId: String? = null
    private var appointmentTypeFlag: String? = null // one appointment or month
    private var appointmentModeFlag: String? = null //alone or with someone

    private lateinit var addAppointmentAloneView: MaterialCardView
    private lateinit var addAppointmentWithSomeoneView: MaterialCardView
    private lateinit var addOneAppointmentView: MaterialCardView
    private lateinit var addMonthAppointmentView: MaterialCardView
    private lateinit var addOneAppointmentFormView: MaterialCardView
    private lateinit var addMonthAppointmentFormView: MaterialCardView
    private lateinit var daysOfWeekDropdown: AutoCompleteTextView
    private lateinit var userRecyclerView: RecyclerView

    private lateinit var setAppointmentStartDateButton: Button
    private lateinit var setAppointmentStartTimeButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coworker = null
        appointmentModeFlag = null
        appointmentTypeFlag = null
        durationTime = null
        startTimeAppointment = null
        startDateAppointment = null
        endDateAppointment = null

        addAppointmentAloneView = view.findViewById(R.id.addAppointmentAloneView)
        addAppointmentWithSomeoneView = view.findViewById(R.id.addAppointmentWithSomeoneView)
        addOneAppointmentView = view.findViewById(R.id.addOneAppointmentView)
        addMonthAppointmentView = view.findViewById(R.id.addMonthAppointmentView)
        addOneAppointmentFormView = view.findViewById(R.id.addOneAppointmentFormView)
        addMonthAppointmentFormView = view.findViewById(R.id.addMonthAppointmentFormView)
        daysOfWeekDropdown = view.findViewById(R.id.daysOfWeekDropdown)
        userRecyclerView = view.findViewById(R.id.userRecyclerView)

        preferenceManager = PreferenceManager(requireContext())
        daysOfWeekDropdown.setOnClickListener {
            daysOfWeekDropdown.showDropDown()
        }

        //Set buttons for adding one appointment form
        setAppointmentStartDateButton = view.findViewById(R.id.setAppointmentStartDateButton)
        val buttonPrevAddOneAppointment = view.findViewById<Button>(R.id.buttonPrevAddOneAppointment)
        val buttonAddOneAppointment = view.findViewById<Button>(R.id.buttonAddOneAppointment)

        buttonPrevAddOneAppointment.setOnClickListener {
            if (appointmentModeFlag == "alone") goBackToAppointmentAloneLayout()
            else goBackToAppointmentWithSomeoneLayout()
        }
        buttonAddOneAppointment.setOnClickListener {
            notes = view.findViewById<TextInputEditText>(R.id.textInputEditTextNotes).text.toString()
            durationTime = view.findViewById<TextInputEditText>(R.id.textInputEditTextDuration).text.toString()

            if (!startDateAppointment.isNullOrEmpty()) addOneAppointment()
            else Toast.makeText(requireContext(), "Musisz dodać datę wizyty.", Toast.LENGTH_SHORT).show()
        }
        setAppointmentStartDateButton.setOnClickListener { setAppointmentDate() }

        //Set buttons for adding month appointment form
        setAppointmentStartTimeButton = view.findViewById(R.id.setAppointmentStartTimeButton)
        val buttonPrevMonth = view.findViewById<Button>(R.id.buttonPrevMonth)
        val buttonAddAppointmentMonth = view.findViewById<Button>(R.id.buttonAddAppointmentMonth)

        buttonPrevMonth.setOnClickListener { goBackToAppointmentAloneLayout() }
        buttonAddAppointmentMonth.setOnClickListener {
            durationTime = view.findViewById<TextInputEditText>(R.id.textInputEditTextDurationMonth).text.toString()

            if (!startTimeAppointment.isNullOrEmpty()) addMonthAppointment()
            else Toast.makeText(requireContext(), "Musisz dodać czas rozpoczęcia wizyty.", Toast.LENGTH_SHORT).show()
        }
        setAppointmentStartTimeButton.setOnClickListener { setAppointmentTime() }


        addAppointmentAloneView.setOnClickListener { setAppointmentAloneLayout() }
        addAppointmentWithSomeoneView.setOnClickListener { setAppointmentWithSomeoneLayout() }
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
        appointmentModeFlag = "alone"
        addAppointmentAloneView.visibility = View.GONE
        addAppointmentWithSomeoneView.visibility = View.GONE
        addMonthAppointmentView.visibility = View.VISIBLE
        addOneAppointmentView.visibility = View.VISIBLE
    }

    private fun goBackToAppointmentAloneLayout(){
        addMonthAppointmentView.visibility = View.VISIBLE
        addOneAppointmentView.visibility = View.VISIBLE
        addOneAppointmentFormView.visibility = View.GONE
        addMonthAppointmentFormView.visibility = View.GONE
    }

    private fun goBackToAppointmentWithSomeoneLayout(){
        addOneAppointmentFormView.visibility = View.GONE
        userRecyclerView.visibility = View.VISIBLE

        if (preferenceManager.getRole() == "Nurse") getDoctorsFromSameHospital()
        else getNursesFromSameHospital()
    }

    private fun setAppointmentWithSomeoneLayout(){
        appointmentModeFlag = "someone"
        addAppointmentAloneView.visibility = View.GONE
        addAppointmentWithSomeoneView.visibility = View.GONE
        userRecyclerView.visibility = View.VISIBLE

        if (preferenceManager.getRole() == "Nurse") getDoctorsFromSameHospital()
        else getNursesFromSameHospital()
    }

    private fun setAddOneAppointmentLayout(){
        appointmentTypeFlag = "one"
        addMonthAppointmentView.visibility = View.GONE
        addOneAppointmentView.visibility = View.GONE
        userRecyclerView.visibility = View.GONE
        addOneAppointmentFormView.visibility = View.VISIBLE
    }

    private fun setAddMonthAppointmentLayout(){
        appointmentTypeFlag = "month"
        addMonthAppointmentView.visibility = View.GONE
        addOneAppointmentView.visibility = View.GONE
        addMonthAppointmentFormView.visibility = View.VISIBLE

        setDaysOfWeekDropdown()
    }


    private fun setDaysOfWeekDropdown(){
        val daysOfWeek = listOf(
            "Poniedziałek",
            "Wtorek",
            "Środa",
            "Czwartek",
            "Piątek",
            "Sobota",
            "Niedziela"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            daysOfWeek
        )

        daysOfWeekDropdown.setAdapter(adapter)
    }

    private fun setUserRecyclerView(){
        recyclerView = userRecyclerView

        userAdapter = UserAdapter(users) { user ->
            coworker = user
            setAddOneAppointmentLayout()
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

            if (appointmentTypeFlag == "one"){
                val date = startDateAppointment
                startDateAppointment += "T${formattedTime}"
                setAppointmentStartDateButton.text = "${convertToDateFormat(date, "yyyy-MM-dd", "dd-MM-yyyy")} ${formattedTime}"
            }
            else{
                startTimeAppointment = formattedTime
                setAppointmentStartTimeButton.text = "${formattedTime}"
            }
        }

        timePicker.show((context as AppCompatActivity).supportFragmentManager, "TIME_PICKER")
    }

    private fun addOneAppointment() {
        val textInputLayoutDuration = view?.findViewById<TextInputLayout>(R.id.textInputLayoutDuration)
        val textInputLayoutNotes = view?.findViewById<TextInputLayout>(R.id.textInputLayoutNotes)

        textInputLayoutDuration?.error = null

        if (durationTime.isNullOrEmpty()) textInputLayoutDuration?.error = "Pole 'Czas trwania wizyty' nie może być puste"
        else {
            if (validateDescription(notes, textInputLayoutNotes)){
                endDateAppointment = countEndTimeAppointment("yyyy-MM-dd'T'HH:mm:ss", startDateAppointment, durationTime)
                if (endDateAppointment != null && currentUserId != null){
                    var doctor: Doctor? = null
                    var nurse: Nurse? = null
                    if (preferenceManager.getRole() == "Doctor"){
                        doctor = Doctor(currentUserId, null, null, null, null)
                        if (coworker != null) nurse = coworker as Nurse?
                    }
                    else {
                        nurse = Nurse(currentUserId, null, null, null)
                        if (coworker != null) doctor = coworker as Doctor?
                    }
                    val appointment = Appointment(null, "AVAILABLE", startDateAppointment,
                        endDateAppointment, doctor, nurse, null, null, notes)
                    navigateToAppointmentDetails(appointment, "singleAppointment")
                }
                else Toast.makeText(requireContext(), "Wystąpił błąd podczas dodawania wizyty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMonthAppointment(){
        val textInputLayoutDuration = view?.findViewById<TextInputLayout>(R.id.textInputLayoutDurationMonth)
        val daysOfWeekMenu = view?.findViewById<TextInputLayout>(R.id.daysOfWeekMenu)
        val selectedDay = daysOfWeekDropdown.text.toString()

        textInputLayoutDuration?.error = null
        daysOfWeekMenu?.error = null

        if (durationTime.isNullOrEmpty()) textInputLayoutDuration?.error = "Pole 'Czas trwania wizyty' nie może być puste"
        else if (selectedDay.isEmpty()) daysOfWeekMenu?.error = "Pole 'Dzień tygodnia' nie może być puste"
        else {
            val addAppointmentCalendarRequest = AddAppointmentCalendarRequest(convertSelectedDay(selectedDay), startTimeAppointment,
                    startTimeAppointment, "PT" + durationTime + "M")
            showAddMonthAppointmentDialog(addAppointmentCalendarRequest)
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

    private fun showAddMonthAppointmentDialog(addAppointmentCalendarRequest: AddAppointmentCalendarRequest){
        showDialog(requireContext(), "Czy na pewno chcesz dodać wizyty w bieżącym miesiącu?") {
            sendNewMonthAppointment(addAppointmentCalendarRequest)
        }
    }

    private fun navigateToAppointmentFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AppointmentsFragment())
    }

    private fun sendNewMonthAppointment(addAppointmentCalendarRequest: AddAppointmentCalendarRequest){
        AppointmentRetrofitInstance.appointmentApiService.addMonthAppointment("Bearer " + preferenceManager.getAuthToken(),
            addAppointmentCalendarRequest)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Wizyty zostały dodane pomyślnie.", Toast.LENGTH_LONG).show()
                        navigateToAppointmentFragment()
                    }
                    else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(context, "Podczas tworzenia wizyt wystąpił błąd: $errorMessage.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getDoctorsFromSameHospital(){
        AppointmentRetrofitInstance.appointmentApiService.getDoctorsFromSameHospital(
            "Bearer " + preferenceManager.getAuthToken().toString())
            .enqueue(object : Callback<List<Doctor>> {
                override fun onResponse(call: Call<List<Doctor>>, response: Response<List<Doctor>>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (!body.isNullOrEmpty()){
                            users = body
                            setUserRecyclerView()
                        }
                    }
                    //else setErrorConnectionLayout()
                }

                override fun onFailure(call: Call<List<Doctor>>, t: Throwable) {
                    //setErrorConnectionLayout()
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getNursesFromSameHospital(){
        AppointmentRetrofitInstance.appointmentApiService.getNursesFromSameHospital(
            "Bearer " + preferenceManager.getAuthToken().toString())
            .enqueue(object : Callback<List<Nurse>> {
                override fun onResponse(call: Call<List<Nurse>>, response: Response<List<Nurse>>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (!body.isNullOrEmpty()){
                            users = body
                            setUserRecyclerView()
                        }
                    }
                    //else setErrorConnectionLayout()
                }

                override fun onFailure(call: Call<List<Nurse>>, t: Throwable) {
                    //setErrorConnectionLayout()
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}