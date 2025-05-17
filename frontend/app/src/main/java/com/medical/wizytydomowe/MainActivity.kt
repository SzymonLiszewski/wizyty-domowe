package com.medical.wizytydomowe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.fragments.AddVisitFragment
import com.medical.wizytydomowe.fragments.prescriptions.PrescriptionsFragment
import com.medical.wizytydomowe.fragments.profile.ProfileFragment
import com.medical.wizytydomowe.fragments.SearchFragment
import com.medical.wizytydomowe.fragments.appointments.AppointmentsFragment
import com.google.android.material.navigation.NavigationBarView
import com.medical.wizytydomowe.fragments.prescriptions.AddPrescriptionFragment
import com.medical.wizytydomowe.fragments.emergency.EmergencyAvailableFragment
import com.medical.wizytydomowe.fragments.profile.LoginFragment
import com.medical.wizytydomowe.fragments.emergency.EmergencyFragment
import com.medical.wizytydomowe.fragments.emergency.EmergencyPatientMenuFragment

class MainActivity : AppCompatActivity(), FragmentNavigation {

    private lateinit var bottomNavigationView: NavigationBarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val preferenceManager = PreferenceManager(this)

        val searchFragment = SearchFragment()
        val appointmentFragment = AppointmentsFragment()
        val addVisitFragment = AddVisitFragment()
        val prescriptionsFragment = PrescriptionsFragment()
        val profileFragment = ProfileFragment()
        val loginFragment = LoginFragment()
        val addPrescriptionFragment = AddPrescriptionFragment()
        val emergencyAvailableFragment = EmergencyAvailableFragment()
        val emergencyFragment = EmergencyFragment()
        val emergencyPatientMenuFragment = EmergencyPatientMenuFragment()

        setStartFragment(preferenceManager)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        setMenuForUser(preferenceManager)

        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bottom_visits -> {
                    setCurrentFragment(appointmentFragment)
                    true
                }
                R.id.bottom_search -> {
                    setCurrentFragment(searchFragment)
                    true
                }
                R.id.bottom_add_visit -> {
                    setCurrentFragment(addVisitFragment)
                    true
                }
                R.id.bottom_prescriptions -> {
                    setCurrentFragment(prescriptionsFragment)
                    true
                }
                R.id.bottom_profile -> {
                    if (preferenceManager.isLoggedIn()) {
                        setCurrentFragment(profileFragment)
                        true
                    }
                    else {
                        setCurrentFragment(loginFragment)
                        true
                    }
                }
                R.id.bottom_medical_report -> {
                    setCurrentFragment(emergencyPatientMenuFragment)
                    true
                }
                R.id.bottom_add_prescription -> {
                    setCurrentFragment(addPrescriptionFragment)
                    true
                }
                R.id.bottom_available_medical_report -> {
                    setCurrentFragment(emergencyAvailableFragment)
                    true
                }
                R.id.bottom_pramedic_medical_reports -> {
                    setCurrentFragment(emergencyFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
            commit()
        }

    override fun navigateToFragment(fragment: Fragment) {
        setCurrentFragment(fragment)
    }

    private fun setMenuPatient() {
        bottomNavigationView.inflateMenu(R.menu.bottom_menu)
    }

    private fun setMenuDoctor() {
        bottomNavigationView.inflateMenu(R.menu.doctor_menu)
    }

    private fun setMenuNurse() {
        bottomNavigationView.inflateMenu(R.menu.nurse_menu)
    }

    private fun setMenuParamedic() {
        bottomNavigationView.inflateMenu(R.menu.paramedic_menu)
    }

    fun setMenuForUser(preferenceManager: PreferenceManager){
        bottomNavigationView.menu.clear()
        if (!preferenceManager.isLoggedIn() || preferenceManager.getRole() == "Patient"){
            setMenuPatient()
        }
        else if (preferenceManager.getRole() == "Doctor"){
            setMenuDoctor()
        }
        else if (preferenceManager.getRole() == "Nurse"){
            setMenuNurse()
        }
        else if (preferenceManager.getRole() == "Paramedic"){
            setMenuParamedic()
        }
    }

    private fun setStartFragment(preferenceManager: PreferenceManager){
        if (!preferenceManager.isLoggedIn() || preferenceManager.getRole() == "Patient"){
            setCurrentFragment(SearchFragment())
        }
        else if (preferenceManager.getRole() == "Doctor"){
            setCurrentFragment(AppointmentsFragment())
        }
        else if (preferenceManager.getRole() == "Nurse"){
            setCurrentFragment(AppointmentsFragment())
        }
        else if (preferenceManager.getRole() == "Paramedic"){
            setCurrentFragment(EmergencyFragment())
        }
    }

}