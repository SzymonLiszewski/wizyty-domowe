package com.medical.wizytydomowe.fragments.emergency

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.fragments.profile.LoginFragment

class EmergencyPatientMenuFragment: Fragment(R.layout.emergency_patient_menu_fragment){

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var addEmergencyView: MaterialCardView
    private lateinit var patientEmergencyView: MaterialCardView
    private lateinit var logoutView: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()

        addEmergencyView = view.findViewById(R.id.addEmergencyView)
        patientEmergencyView = view.findViewById(R.id.patientEmergencyView)
        logoutView = view.findViewById(R.id.logoutView)

        val goToLoginButton = view.findViewById<Button>(R.id.goToLoginButton)

        goToLoginButton.setOnClickListener {
            navigateToLoginFragment()
        }

        patientEmergencyView.setOnClickListener {
            navigateToEmergencyFragment()
        }

        addEmergencyView.setOnClickListener {
            navigateToAddEmergencyFragment()
        }

        if (userToken != null) setLogInLayout()
        else setLogoutLayout()
    }

    private fun setLogInLayout(){
        addEmergencyView.visibility = View.VISIBLE
        logoutView.visibility = View.GONE
        patientEmergencyView.visibility = View.VISIBLE
    }

    private fun setLogoutLayout(){
        addEmergencyView.visibility = View.GONE
        logoutView.visibility = View.VISIBLE
        patientEmergencyView.visibility = View.GONE
    }

    private fun navigateToLoginFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(LoginFragment())
    }

    private fun navigateToEmergencyFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EmergencyFragment())
    }

    private fun navigateToAddEmergencyFragment(){
        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(AddEmergencyFragment())
    }

}