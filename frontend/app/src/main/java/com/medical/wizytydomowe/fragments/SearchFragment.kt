package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.users.Doctor
import com.medical.wizytydomowe.api.users.DoctorAdapter

class SearchFragment : Fragment(R.layout.search_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorAdapter
    private lateinit var doctorsDisplayed: MutableList<Doctor>
    private lateinit var allDoctors: List<Doctor>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rvDoctors)
        val searchButton = view.findViewById<ImageButton>(R.id.imgBtnSearch)
        val specializationSelect = view.findViewById<Spinner>(R.id.sp_specialization)
        val searchEditText = view.findViewById<EditText>(R.id.et_search)

        // Example data
        val exampleDoctors = listOf(
            Doctor("1", "Jan", "Kowalski", "laryngolog", "Szpital Miejski w Gdańsku"),
            Doctor("2", "Robert", "Nowak", "ginekolog", "Szpital Miejski w Krakowie"),
            Doctor("3", "Franciszek", "Wiśniewski", "urolog", "Szpital Miejski w Warszawie"),
            Doctor("4", "Wojciech", "Wójcik", "chirurg", "Szpital Miejski w Gdańsku"),
            Doctor("5", "Paweł", "Lewandowski", "laryngolog", "Szpital Miejski w Krakowie"),
            Doctor("6", "Adrian", "Kowalczyk", "laryngolog", "Szpital Miejski w Gdańsku"),
            Doctor("7", "Szymon", "Kamiński", "pediatra", "Szpital Miejski w Poznaniu"),
            Doctor("8", "Arkadiusz", "Zieliński", "pediatra", "Szpital Miejski w Gdańsku"),
            Doctor("9", "Janusz", "Szymański", "pediatra", "Szpital Miejski w Warszawie"),
            Doctor("10", "Robert", "Woźniak", "psycholog", "Szpital Miejski w Gdańsku")
        )

        doctorsDisplayed = exampleDoctors.toMutableList()
        allDoctors = exampleDoctors

        // Initialize adapter
        adapter = DoctorAdapter(doctorsDisplayed) { doctor ->
            val bundle = Bundle().apply {
                putSerializable("doctor", doctor)
            }

            val bookVisitFragment = BookVisitFragment().apply {
                arguments = bundle
            }
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(bookVisitFragment)
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        searchButton.setOnClickListener{
            val searchQuery = searchEditText.text.toString().trim()
            val selectedSpecialization = specializationSelect.selectedItem.toString()
            doctorsDisplayed.clear()

            for (doctor in allDoctors){
                if (selectedSpecialization.equals("wszyscy", ignoreCase = true)){
                    if (searchQuery.isEmpty()){
                        doctorsDisplayed.add(doctor)
                    }
                    else if (doctor.firstName.toString().contains(searchQuery.trim(), ignoreCase = true)
                        || doctor.lastName.toString().contains(searchQuery.trim(), ignoreCase = true)){
                        doctorsDisplayed.add(doctor)
                    }
                }
                else if (selectedSpecialization.equals(doctor.specialization, ignoreCase = true)){
                    if (searchQuery.isEmpty()){
                        doctorsDisplayed.add(doctor)
                    }
                    else if (doctor.firstName.toString().contains(searchQuery.trim(), ignoreCase = true)
                        || doctor.lastName.toString().contains(searchQuery.trim(), ignoreCase = true)){
                        doctorsDisplayed.add(doctor)
                    }
                }
            }

            adapter.updateDoctors(doctorsDisplayed)

            if (doctorsDisplayed.isEmpty()) {
                Toast.makeText(requireContext(), "Nie znaleziono lekarzy.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}