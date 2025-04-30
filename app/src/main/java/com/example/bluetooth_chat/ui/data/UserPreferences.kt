package com.example.bluetooth_chat.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_STATUS = "status"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    var username: String
        get() = prefs.getString(KEY_USERNAME, "Sophia") ?: "Sophia"
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    var status: String
        get() = prefs.getString(KEY_STATUS, "Available to chat") ?: "Available to chat"
        set(value) = prefs.edit().putString(KEY_STATUS, value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    fun isDarkModeSet(): Boolean {
        return prefs.contains(KEY_DARK_MODE)
    }
}