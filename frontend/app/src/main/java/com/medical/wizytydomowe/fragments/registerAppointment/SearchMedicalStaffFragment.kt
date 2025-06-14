package com.medical.wizytydomowe.fragments.registerAppointment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointmentApi.AppointmentRetrofitInstance
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.Nurse
import com.medical.wizytydomowe.api.users.User
import com.medical.wizytydomowe.api.users.UserAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMedicalStaffFragment : Fragment(R.layout.search_medical_staff_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    private lateinit var noMedicalStaffView: MaterialCardView
    private lateinit var errorConnectionView: MaterialCardView
    private lateinit var medicalStaffRecyclerView: RecyclerView
    private lateinit var filterMedicalStaffView: MaterialCardView
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipSpecializationGroup: ChipGroup


    private var errorConnectionFlag: Boolean = false
    private var responsesReceived: Int = 0
    private var expectedResponses: Int = 2

    private var users: MutableList<User> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterMedicalStaffButton = view.findViewById<Button>(R.id.filterMedicalStaffButton)
        filterMedicalStaffButton.setOnClickListener { searchMedicalStaffWithFilters() }

        noMedicalStaffView = view.findViewById(R.id.noMedicalStaffView)
        errorConnectionView = view.findViewById(R.id.errorConnectionView)
        medicalStaffRecyclerView = view.findViewById(R.id.medicalStaffRecyclerView)
        filterMedicalStaffView = view.findViewById(R.id.filterMedicalStaffView)
        chipGroup = view.findViewById(R.id.chipGroup)
        chipSpecializationGroup = view.findViewById(R.id.chipSpecializationGroup)

        sendMedicalStaffRequests(null, null, null)
    }

    private fun searchMedicalStaffWithFilters(){
        val textInputLayoutMedicalStaffFilter = view?.findViewById<TextInputLayout>(R.id.textInputLayoutMedicalStaffFilter)
        val textInputEditTextMedicalStaffFilter = view?.findViewById<TextInputEditText>(R.id.textInputEditTextMedicalStaffFilter)

        val query = textInputEditTextMedicalStaffFilter?.text.toString()

        var specialization: String? = null
        when (chipSpecializationGroup.checkedChipId) {
            R.id.chipNurse -> specialization = "Nurse"
            R.id.chipLekarzRodzinny -> specialization = "LekarzRodzinny"
            R.id.chipPediatra -> specialization = "Pediatra"
            R.id.chipInternista -> specialization = "Internista"
            R.id.chipKardiolog -> specialization = "Kardiolog"
            R.id.chipPsycholog -> specialization = "Psycholog"
            R.id.chipGeneralPractitioner -> specialization = "General Practitioner"
        }

        if (query.isNullOrEmpty()) sendMedicalStaffRequests(null, null, specialization)
        else {
            when (chipGroup.checkedChipId) {
                R.id.chipName -> sendMedicalStaffRequests(null, query, specialization)
                R.id.chipCity -> sendMedicalStaffRequests(query, null, specialization)
                else -> sendMedicalStaffRequests(null, null, specialization)
            }
        }
    }

    private fun sendMedicalStaffRequests(preferredCity: String?, preferredLastName: String?, preferredSpecialization: String?){
        users = mutableListOf()
        errorConnectionFlag = false

        if (preferredSpecialization != null && preferredSpecialization == "Nurse") {
            responsesReceived = 1
            getAvailableNurses(preferredCity, preferredLastName)
        }
        else if (preferredSpecialization != null) {
            responsesReceived = 1
            getAvailableDoctors(preferredCity, preferredLastName, preferredSpecialization)
        }
        else {
            responsesReceived = 0
            getAvailableDoctors(preferredCity, preferredLastName, preferredSpecialization)
            getAvailableNurses(preferredCity, preferredLastName)
        }

    }

    private fun setMedicalStaffLayout(){
        if (!errorConnectionFlag){
            if (users.isEmpty()) setNoMedicalStaffLayout()
            else {
                noMedicalStaffView.visibility = View.GONE
                errorConnectionView.visibility = View.GONE
                medicalStaffRecyclerView.visibility = View.VISIBLE
                filterMedicalStaffView.visibility = View.VISIBLE

                recyclerView = medicalStaffRecyclerView

                adapter = UserAdapter(users) { user ->
                    if (user is Nurse) navigateToSearchAppointmentFragment(user.id.toString(), "nurse")
                    else navigateToSearchAppointmentFragment(user.id.toString(), "doctor")
                }

                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }
    }

    private fun navigateToSearchAppointmentFragment(id: String, role: String){
        val bundle = Bundle().apply {
            putSerializable("id", id)
            putSerializable("role", role)
        }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(SearchAppointmentFragment().apply { arguments = bundle })
    }

    private fun setErrorConnectionLayout(){
        noMedicalStaffView.visibility = View.GONE
        errorConnectionView.visibility = View.VISIBLE
        medicalStaffRecyclerView.visibility = View.GONE
        filterMedicalStaffView.visibility = View.GONE
    }

    private fun setNoMedicalStaffLayout(){
        noMedicalStaffView.visibility = View.VISIBLE
        errorConnectionView.visibility = View.GONE
        medicalStaffRecyclerView.visibility = View.GONE
        filterMedicalStaffView.visibility = View.VISIBLE
    }

    private fun addMedicalStaffToList(newUsers: List<User>){
        for (u in newUsers){
            if (u != null) users.add(u)
        }
        responsesReceived++
        if (responsesReceived == expectedResponses) {
            setMedicalStaffLayout()
        }
    }

    private fun getAvailableDoctors(preferredCity: String?, preferredLastName: String?, preferredSpecialization: String?){
        AppointmentRetrofitInstance.appointmentApiService.getDoctorAvailable(preferredCity, preferredLastName, preferredSpecialization)
            .enqueue(object : Callback<List<Doctor>> {
            override fun onResponse(call: Call<List<Doctor>>, response: Response<List<Doctor>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (!body.isNullOrEmpty()) addMedicalStaffToList(body)
                    else addMedicalStaffToList(emptyList())
                }
                else {
                    setErrorConnectionLayout()
                    errorConnectionFlag = true
                }
            }

            override fun onFailure(call: Call<List<Doctor>>, t: Throwable) {
                setErrorConnectionLayout()
                errorConnectionFlag = true
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getAvailableNurses(preferredCity: String?, preferredLastName: String?){
        AppointmentRetrofitInstance.appointmentApiService.getNursesAvailable(preferredCity, preferredLastName)
            .enqueue(object : Callback<List<Nurse>> {
                override fun onResponse(call: Call<List<Nurse>>, response: Response<List<Nurse>>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (!body.isNullOrEmpty()) addMedicalStaffToList(body)
                        else addMedicalStaffToList(emptyList())
                    }
                    else {
                        setErrorConnectionLayout()
                        errorConnectionFlag = true
                    }
                }

                override fun onFailure(call: Call<List<Nurse>>, t: Throwable) {
                    setErrorConnectionLayout()
                    errorConnectionFlag = true
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}