package com.medical.wizytydomowe.fragments.registerAppointment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
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

    private var errorConnectionFlag: Boolean = false
    private var responsesReceived: Int = 0
    private var expectedResponses: Int = 2

    private var users: MutableList<User> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        responsesReceived = 0
        errorConnectionFlag = false
        users = mutableListOf()

        noMedicalStaffView = view.findViewById(R.id.noMedicalStaffView)
        errorConnectionView = view.findViewById(R.id.errorConnectionView)
        medicalStaffRecyclerView = view.findViewById(R.id.medicalStaffRecyclerView)

        sendMedicalStaffRequests()
    }

    private fun sendMedicalStaffRequests(){
        getAvailableDoctors()
        getAvailableNurses()
    }

    private fun setMedicalStaffLayout(){
        if (!errorConnectionFlag){
            if (users.isEmpty()) setNoMedicalStaffLayout()
            else {
                noMedicalStaffView.visibility = View.GONE
                errorConnectionView.visibility = View.GONE
                medicalStaffRecyclerView.visibility = View.VISIBLE

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
    }

    private fun setNoMedicalStaffLayout(){
        noMedicalStaffView.visibility = View.VISIBLE
        errorConnectionView.visibility = View.GONE
        medicalStaffRecyclerView.visibility = View.GONE
    }

    @Synchronized
    private fun addMedicalStaffToList(newUsers: List<User>){
        for (u in newUsers){
            if (u != null) users.add(u)
        }
        responsesReceived++
        if (responsesReceived == expectedResponses) {
            setMedicalStaffLayout()
        }
    }

    private fun getAvailableDoctors(){
        AppointmentRetrofitInstance.appointmentApiService.getDoctorAvailable()
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

    private fun getAvailableNurses(){
        AppointmentRetrofitInstance.appointmentApiService.getNursesAvailable()
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