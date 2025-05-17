package com.medical.wizytydomowe.fragments.editProfile

import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R

class EditProfileFragment: Fragment(R.layout.edit_profile_fragment) {

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var editPatientGrid: GridLayout
    private lateinit var editMedicalStaffGrid: GridLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        val userRole = preferenceManager.getRole()

        editPatientGrid = view.findViewById(R.id.editPatientGrid)
        editMedicalStaffGrid = view.findViewById(R.id.editMedicalStaffGrid)

        val editPersonalDataPatientView = view.findViewById<MaterialCardView>(R.id.editPersonalDataPatientView)
        val editContactDataPatientView = view.findViewById<MaterialCardView>(R.id.editContactDataPatientView)
        val editDateOfBirthPatientView = view.findViewById<MaterialCardView>(R.id.editDateOfBirthPatientView)
        val editAddressPatientView = view.findViewById<MaterialCardView>(R.id.editAddressPatientView)
        val editPersonalDataMedicalStaffView = view.findViewById<MaterialCardView>(R.id.editPersonalDataMedicalStaffView)
        val editContactDataMedicalStaffView = view.findViewById<MaterialCardView>(R.id.editContactDataMedicalStaffView)

        editPersonalDataMedicalStaffView.setOnClickListener {
            navigateToEditDataFragment("personalData")
        }

        editContactDataMedicalStaffView.setOnClickListener {
            navigateToEditDataFragment("contact")
        }

        editPersonalDataPatientView.setOnClickListener {
            navigateToEditDataFragment("personalData")
        }

        editContactDataPatientView.setOnClickListener {
            navigateToEditDataFragment("contact")
        }

        editDateOfBirthPatientView.setOnClickListener {
            navigateToEditDataFragment("dateOfBirth")
        }

        editAddressPatientView.setOnClickListener {
            navigateToEditDataFragment("address")
        }

        if (userRole == "Patient") setPatientLayout()
        else setMedicalStaffLayout()
    }

    private fun setPatientLayout(){
        editPatientGrid.visibility = View.VISIBLE
        editMedicalStaffGrid.visibility = View.GONE
    }

    private fun setMedicalStaffLayout(){
        editPatientGrid.visibility = View.GONE
        editMedicalStaffGrid.visibility = View.VISIBLE
    }

    private fun navigateToEditDataFragment(editType: String){
        val bundle = Bundle().apply { putSerializable("editType", editType) }

        val activity = activity as? FragmentNavigation
        activity?.navigateToFragment(EditProfileFormsFragment().apply { arguments = bundle })
    }
}