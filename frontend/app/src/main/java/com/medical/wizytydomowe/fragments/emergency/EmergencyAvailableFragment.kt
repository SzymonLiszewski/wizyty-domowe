package com.medical.wizytydomowe.fragments.emergency

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
import com.medical.wizytydomowe.api.emergency.Emergency
import com.medical.wizytydomowe.api.emergency.EmergencyAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmergencyAvailableFragment : Fragment(R.layout.emergency_available_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmergencyAdapter

    private lateinit var noEmergencyView: MaterialCardView
    private lateinit var emergencyRecyclerView: RecyclerView
    private lateinit var errorConnectionView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emergencyRecyclerView = view.findViewById(R.id.emergencyRecyclerView)
        noEmergencyView = view.findViewById(R.id.noEmergencyView)
        errorConnectionView = view.findViewById(R.id.errorConnectionView)

        getAvailableEmergency()
    }

    private fun setErrorConnectionLayout(){
        noEmergencyView.visibility = View.GONE
        emergencyRecyclerView.visibility = View.GONE
        errorConnectionView.visibility = View.VISIBLE
    }

    private fun setNoAvailableEmergencyLayout(){
        noEmergencyView.visibility = View.VISIBLE
        emergencyRecyclerView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE
    }

    private fun setEmergencyLayout(emergencies: List<Emergency>){
        noEmergencyView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE
        emergencyRecyclerView.visibility = View.VISIBLE

        recyclerView = emergencyRecyclerView

        adapter = EmergencyAdapter(emergencies) { emergency ->
            navigateToEmergencyDetailsFragment(emergency)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun navigateToEmergencyDetailsFragment(emergency: Emergency){
        val bundle = Bundle().apply { putSerializable("emergency", emergency) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyDetailsFragment().apply { arguments = bundle })
    }

    private fun getAvailableEmergency(){
        AppointmentRetrofitInstance.appointmentApiService.getAvailableEmergency()
            .enqueue(object : Callback<List<Emergency>> {
                override fun onResponse(call: Call<List<Emergency>>, response: Response<List<Emergency>>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (!body.isNullOrEmpty()){
                            setEmergencyLayout(body)
                        }
                        else setNoAvailableEmergencyLayout()
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