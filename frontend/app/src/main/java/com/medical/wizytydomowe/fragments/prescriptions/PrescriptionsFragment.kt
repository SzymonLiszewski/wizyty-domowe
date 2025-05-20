package com.medical.wizytydomowe.fragments.prescriptions

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.appointmentApi.AppointmentRetrofitInstance
import com.medical.wizytydomowe.api.prescriptions.Prescription
import com.medical.wizytydomowe.api.prescriptions.PrescriptionAdapter
import com.medical.wizytydomowe.fragments.SearchFragment
import com.medical.wizytydomowe.fragments.profile.LoginFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrescriptionsFragment : Fragment(R.layout.prescriptions_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrescriptionAdapter

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var logoutView: MaterialCardView
    private lateinit var noPrescriptionView: MaterialCardView
    private lateinit var errorConnectionView: MaterialCardView
    private lateinit var prescriptionsRecyclerView: RecyclerView
    private lateinit var goToAddPrescriptionButton: Button
    private lateinit var goToMakeAnAppointmentButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()

        logoutView = view.findViewById(R.id.logoutView)
        noPrescriptionView = view.findViewById(R.id.noPrescriptionView)
        errorConnectionView = view.findViewById(R.id.errorConnectionView)
        prescriptionsRecyclerView = view.findViewById(R.id.prescriptionsRecyclerView)

        val goToLoginButton = view.findViewById<Button>(R.id.goToLoginButton)
        goToAddPrescriptionButton = view.findViewById(R.id.goToAddPrescriptionButton)
        goToMakeAnAppointmentButton = view.findViewById(R.id.goToMakeAnAppointmentButton)

        goToLoginButton.setOnClickListener {
            navigateToLoginFragment()
        }

        goToAddPrescriptionButton.setOnClickListener {
            navigateToAddPrescriptionFragment()
        }

        goToMakeAnAppointmentButton.setOnClickListener {
            navigateToMakeAnAppointmentFragment()
        }

        if (preferenceManager.getAuthToken() != null) {
            logoutView.visibility = View.GONE

            val userToken = "Bearer " + preferenceManager.getAuthToken()
            if (userRole == "Patient") getPatientPrescriptions(userToken)
            else getDoctorPrescriptions(userToken)
        }
        else{
            setLogoutLayout()
        }
    }

    private fun setPrescriptionsLayout(prescriptions: List<Prescription>){
        prescriptionsRecyclerView.visibility = View.VISIBLE
        noPrescriptionView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE

        recyclerView = prescriptionsRecyclerView

        adapter = PrescriptionAdapter(prescriptions) { prescription ->
            navigateToPrescriptionDetailsFragment(prescription)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setNoPrescriptionsLayout(userRole: String?, goToAddPrescriptionButton: Button,
                                         goToMakeAnAppointmentButton: Button){
        noPrescriptionView.visibility = View.VISIBLE
        prescriptionsRecyclerView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE

        if (userRole == "Patient"){
            goToMakeAnAppointmentButton.visibility = View.VISIBLE
            goToAddPrescriptionButton.visibility = View.GONE
        }
        else{
            goToMakeAnAppointmentButton.visibility = View.GONE
            goToAddPrescriptionButton.visibility = View.VISIBLE
        }
    }

    private fun setLogoutLayout(){
        logoutView.visibility = View.VISIBLE
        noPrescriptionView.visibility = View.GONE
        prescriptionsRecyclerView.visibility = View.GONE
        errorConnectionView.visibility = View.GONE
    }

    private fun setErrorConnectionLayout(){
        logoutView.visibility = View.GONE
        noPrescriptionView.visibility = View.GONE
        prescriptionsRecyclerView.visibility = View.GONE
        errorConnectionView.visibility = View.VISIBLE
    }

    private fun navigateToLoginFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(LoginFragment())
    }

    private fun navigateToMakeAnAppointmentFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(SearchFragment())
    }

    private fun navigateToAddPrescriptionFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AddPrescriptionFragment())
    }

    private fun navigateToPrescriptionDetailsFragment(prescription: Prescription){
        val bundle = Bundle().apply { putSerializable("prescription", prescription) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(PrescriptionDetailsFragment().apply { arguments = bundle })
    }

    private fun getDoctorPrescriptions(token: String?){
        AppointmentRetrofitInstance.appointmentApiService.getDoctorPrescriptions(token.toString())
            .enqueue(object : Callback<List<Prescription>> {
            override fun onResponse(call: Call<List<Prescription>>, response: Response<List<Prescription>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (!body.isNullOrEmpty()){
                        setPrescriptionsLayout(body)
                    }
                    else setNoPrescriptionsLayout(preferenceManager.getRole(), goToAddPrescriptionButton, goToMakeAnAppointmentButton)
                }
                else {
                    setErrorConnectionLayout()
                }
            }

            override fun onFailure(call: Call<List<Prescription>>, t: Throwable) {
                setErrorConnectionLayout()
                Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPatientPrescriptions(token: String?){
        AppointmentRetrofitInstance.appointmentApiService.getPatientPrescriptions(token.toString())
            .enqueue(object : Callback<List<Prescription>> {
                override fun onResponse(call: Call<List<Prescription>>, response: Response<List<Prescription>>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (!body.isNullOrEmpty()){
                            setPrescriptionsLayout(body)
                        }
                        else setNoPrescriptionsLayout(preferenceManager.getRole(), goToAddPrescriptionButton, goToMakeAnAppointmentButton)
                    }
                    else {
                        setErrorConnectionLayout()
                    }
                }

                override fun onFailure(call: Call<List<Prescription>>, t: Throwable) {
                    setErrorConnectionLayout()
                    Toast.makeText(context, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}