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
import com.medical.wizytydomowe.fragments.LoginFragment

class MainActivity : AppCompatActivity(), FragmentNavigation {
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
        val profileFragment = ProfileFragment()
        val loginFragment = LoginFragment()

        setCurrentFragment(visitFragment)

        val bottomNavigationView: NavigationBarView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bottom_visits -> {
                    setCurrentFragment(visitFragment)
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
}