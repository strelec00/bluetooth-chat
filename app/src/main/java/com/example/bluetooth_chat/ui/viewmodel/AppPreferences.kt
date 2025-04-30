package com.example.bluetooth_chat.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Utility class to manage application preferences using SharedPreferences
 */
object AppPreferences {
    // Name of SharedPreferences file
    private const val PREF_NAME = "bluetooth_chat_prefs"

    // Keys for different preferences
    private const val KEY_FIRST_LAUNCH = "is_first_launch"
    
    /**
     * Get the SharedPreferences instance
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Check if this is the first time the app has been launched
     * @return true if first launch, false otherwise
     */
    fun isFirstLaunch(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_FIRST_LAUNCH, true)
    }

    /**
     * Mark the first launch as complete
     */
    fun setFirstLaunchComplete(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    /**
     * Reset first launch status (for testing)
     */
    fun resetFirstLaunch(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_FIRST_LAUNCH, true).apply()
    }
}