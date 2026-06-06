package com.example.techstoreapp.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("techstore_session", Context.MODE_PRIVATE)

    fun saveSession(
        token: String,
        email: String,
        role: String
    ) {
        preferences.edit()
            .putString("token", token)
            .putString("email", email)
            .putString("role", role)
            .apply()
    }

    fun getToken(): String? {
        return preferences.getString("token", null)
    }

    fun getBearerToken(): String? {
        val token = getToken()
        return if (token != null) {
            "Bearer $token"
        } else {
            null
        }
    }

    fun getEmail(): String? {
        return preferences.getString("email", null)
    }

    fun getRole(): String? {
        return preferences.getString("role", null)
    }

    fun isAdmin(): Boolean {
        return getRole() == "ADMIN"
    }

    fun isCustomer(): Boolean {
        return getRole() == "CUSTOMER"
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun clearSession() {
        preferences.edit().clear().apply()
    }
}