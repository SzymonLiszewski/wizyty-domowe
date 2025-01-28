package com.medical.wizytydomowe.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medical.wizytydomowe.FragmentNavigation
import com.medical.wizytydomowe.MainActivity
import com.medical.wizytydomowe.PreferenceManager
import com.medical.wizytydomowe.R
import com.medical.wizytydomowe.api.RetrofitInstance
import retrofit2.Callback
import com.medical.wizytydomowe.api.userInfo.UserInfoResponse
import retrofit2.Call
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.profile_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferenceManager = PreferenceManager(requireContext())
        val userToken = preferenceManager.getAuthToken()

        val userInformationTable = view.findViewById<TableLayout>(R.id.tbl_user_info)
        val logoutButton= view.findViewById<Button>(R.id.btn_logout)
        val userNotLoggedInTextView = view.findViewById<TextView>(R.id.tv_user_not_logged_in)

        if (userToken != null) {
            val requestToken = "Bearer $userToken"
            RetrofitInstance.apiService.getUserInfo(requestToken)
                .enqueue(object : Callback<UserInfoResponse> {
                    override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()

                            userInformationTable.visibility = View.VISIBLE
                            logoutButton.visibility = View.VISIBLE
                            userNotLoggedInTextView.visibility = View.GONE

                            view.findViewById<TextView>(R.id.tv_user_full_name).text =
                                "${responseBody?.firstName} ${responseBody?.lastName}"

                            view.findViewById<TextView>(R.id.tv_user_email).text =
                                "${responseBody?.email}"

                            if (responseBody?.address != null) {
                                view.findViewById<TextView>(R.id.tv_user_address).text =
                                    "${responseBody.address}"
                            }


                        } else {
                            Log.e("API", "Błąd: ${response.code()} - ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                        // Obsługa błędu sieci
                        Log.e("API", "Niepowodzenie: ${t.message}")
                    }
                })
        }

        logoutButton.setOnClickListener {

            val preferenceManager = PreferenceManager(requireContext())
            preferenceManager.clearAuthToken()
            preferenceManager.clearRole()

            Toast.makeText(context, "Wylogowano.", Toast.LENGTH_SHORT).show()

            userInformationTable.visibility = View.GONE
            logoutButton.visibility = View.GONE
            userNotLoggedInTextView.visibility = View.VISIBLE

            (activity as? MainActivity)?.setMenuForUser(PreferenceManager(requireContext()))

            val searchFragment = SearchFragment()
            val activity = activity as? FragmentNavigation
            activity?.navigateToFragment(searchFragment)
        }


    }
}