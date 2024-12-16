package com.medical.wizytydomowe

import android.content.Context

class PreferenceManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("authToken", token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString("authToken", null)
    }

    fun clearAuthToken() {
        val editor = sharedPreferences.edit()
        editor.remove("authToken")
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrEmpty()
    }
}
