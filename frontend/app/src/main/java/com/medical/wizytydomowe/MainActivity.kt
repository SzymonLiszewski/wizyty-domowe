package com.medical.wizytydomowe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.fragments.AddVisitFragment
import com.medical.wizytydomowe.fragments.PrescriptionsFragment
import com.medical.wizytydomowe.fragments.ProfileFragment
import com.medical.wizytydomowe.fragments.SearchFragment
import com.medical.wizytydomowe.fragments.VisitsFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.medical.wizytydomowe.fragments.AddMedicalReportFragment
import com.medical.wizytydomowe.fragments.AddPrescriptionFragment
import com.medical.wizytydomowe.fragments.AvailableMedicalReportsFragment
import com.medical.wizytydomowe.fragments.LoginFragment
import com.medical.wizytydomowe.fragments.ParamedicMedicalReportsFragment
import com.medical.wizytydomowe.fragments.PrescriptionsLogoutFragment
import com.medical.wizytydomowe.fragments.VisitsLogoutFragment

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
        val visitFragment = VisitsFragment()
        val addVisitFragment = AddVisitFragment()
        val prescriptionsFragment = PrescriptionsFragment()
        val prescriptionsLogoutFragment = PrescriptionsLogoutFragment()
        val profileFragment = ProfileFragment()
        val loginFragment = LoginFragment()
        val visitLogoutFragment = VisitsLogoutFragment()
        val addMedicalReport = AddMedicalReportFragment()
        val addPrescriptionFragment = AddPrescriptionFragment()
        val availableMedicalReportsFragment = AvailableMedicalReportsFragment()
        val paramedicMedicalReportsFragment = ParamedicMedicalReportsFragment()

        setCurrentFragment(visitLogoutFragment)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        setMenuForUser(preferenceManager)

        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bottom_visits -> {
                    if (preferenceManager.isLoggedIn()) {
                        setCurrentFragment(visitFragment)
                        true
                    }
                    else {
                        setCurrentFragment(visitLogoutFragment)
                        true
                    }
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
                    if (preferenceManager.isLoggedIn()) {
                        setCurrentFragment(prescriptionsFragment)
                        true
                    }
                    else {
                        setCurrentFragment(prescriptionsLogoutFragment)
                        true
                    }
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
                    setCurrentFragment(addMedicalReport)
                    true
                }
                R.id.bottom_add_prescription -> {
                    setCurrentFragment(addPrescriptionFragment)
                    true
                }
                R.id.bottom_available_medical_report -> {
                    setCurrentFragment(availableMedicalReportsFragment)
                    true
                }
                R.id.bottom_pramedic_medical_reports -> {
                    setCurrentFragment(paramedicMedicalReportsFragment)
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

}