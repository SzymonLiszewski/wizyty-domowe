package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R

class PrescriptionsLogoutFragment : Fragment(R.layout.prescriptions_logout_fragment)  {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToLoginButton = view.findViewById<Button>(R.id.btn_go_to_login)

        goToLoginButton.setOnClickListener {
            val loginFragment = LoginFragment()
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(loginFragment)
        }
    }

}