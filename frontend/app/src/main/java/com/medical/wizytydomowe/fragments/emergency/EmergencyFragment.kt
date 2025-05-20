package com.medical.wizytydomowe.fragments.emergency

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointmentApi.AppointmentRetrofitInstance
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.emergency.EmergencyAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmergencyFragment  : Fragment(R.layout.emergency_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmergencyAdapter

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var noEmergencyView: MaterialCardView
    private lateinit var emergencyRecyclerView: RecyclerView
    private lateinit var goToAddEmergencyButton: Button
    private lateinit var goToAvailableEmergencyButton: Button
    private lateinit var errorConnectionView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()
        val userToken = "Bearer " + preferenceManager.getAuthToken()

        emergencyRecyclerView = view.findViewById(R.id.emergencyRecyclerView)
        noEmergencyView = view.findViewById(R.id.noEmergencyView)
        goToAvailableEmergencyButton = view.findViewById(R.id.goToAvailableEmergencyButton)
        goToAddEmergencyButton = view.findViewById(R.id.goToAddEmergencyButton)
        errorConnectionView = view.findViewById(R.id.errorConnectionView)

        goToAvailableEmergencyButton.setOnClickListener {
            navigateToAvailableFragment()
        }

        goToAddEmergencyButton.setOnClickListener {
            navigateToAddEmergencyFragment()
        }


        if (userRole == "Patient") getPatientEmergency(userToken, userRole)
        else getParamedicEmergency(userToken, userRole)
    }

    private fun navigateToEmergencyDetailsFragment(emergency: Emergency){
        val bundle = Bundle().apply { putSerializable("emergency", emergency) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyDetailsFragment().apply { arguments = bundle })
    }

    private fun navigateToAvailableFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyAvailableFragment())
    }

    private fun setErrorConnectionLayout(){
        emergencyRecyclerView.visibility = View.GONE
        noEmergencyView.visibility = View.GONE
        errorConnectionView.visibility = View.VISIBLE
    }

    private fun setNoEmergenciesLayout(userRole: String?){
        emergencyRecyclerView.visibility = View.GONE
        noEmergencyView.visibility = View.VISIBLE
        errorConnectionView.visibility = View.GONE

        if (userRole == "Patient"){
            goToAddEmergencyButton.visibility = View.VISIBLE
            goToAvailableEmergencyButton.visibility = View.GONE
        }
        else{
            goToAddEmergencyButton.visibility = View.GONE
            goToAvailableEmergencyButton.visibility = View.VISIBLE
        }
    }

    private fun setEmergenciesLayout(emergencies: List<Emergency>){
        emergencyRecyclerView.visibility = View.VISIBLE
        noEmergencyView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE

        recyclerView = emergencyRecyclerView

        adapter = EmergencyAdapter(emergencies) { emergency ->
            navigateToEmergencyDetailsFragment(emergency)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun navigateToAddEmergencyFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AddEmergencyFragment())
    }

    private fun getPatientEmergency(userToken: String?, userRole: String?){
        AppointmentRetrofitInstance.appointmentApiService.getPatientEmergency(userToken.toString())
            .enqueue(object : Callback<List<Emergency>> {
                override fun onResponse(call: Call<List<Emergency>>, response: Response<List<Emergency>>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (!body.isNullOrEmpty()){
                            setEmergenciesLayout(body)
                        }
                        else setNoEmergenciesLayout(userRole)
                    }
                    else setErrorConnectionLayout()
                }

                override fun onFailure(call: Call<List<Emergency>>, t: Throwable) {
                    setErrorConnectionLayout()
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getParamedicEmergency(userToken: String?, userRole: String?){
        AppointmentRetrofitInstance.appointmentApiService.getParamedicEmergency(userToken.toString())
            .enqueue(object : Callback<List<Emergency>> {
                override fun onResponse(call: Call<List<Emergency>>, response: Response<List<Emergency>>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (!body.isNullOrEmpty()){
                            setEmergenciesLayout(body)
                        }
                        else setNoEmergenciesLayout(userRole)
                    }
                    else setErrorConnectionLayout()
                }

                override fun onFailure(call: Call<List<Emergency>>, t: Throwable) {
                    setErrorConnectionLayout()
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}