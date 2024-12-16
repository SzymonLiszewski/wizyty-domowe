package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.R

class LoginFragment : Fragment(R.layout.login_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerTextView = view.findViewById<TextView>(R.id.tv_register)
        registerTextView.setOnClickListener {
            val registerFragment = RegisterFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, registerFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}