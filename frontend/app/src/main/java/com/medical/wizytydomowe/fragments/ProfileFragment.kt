package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R

class ProfileFragment : Fragment(R.layout.profile_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()

        val tokenView = view.findViewById<TextView>(R.id.tv_user_token)

        if (userToken != null) {
            tokenView.text = "Token użytkownika: $userToken"
        }

        val logoutButton= view.findViewById<Button>(R.id.btn_logout)
        logoutButton.setOnClickListener {

            val preferenceManager = PreferenceManager(requireContext())
            preferenceManager.clearAuthToken()

            Toast.makeText(context, "Wylogowano.", Toast.LENGTH_SHORT).show()

            val searchFragment = SearchFragment()
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(searchFragment)
        }


    }
}