package com.example.bluetooth_chat.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.bluetooth_chat.data.UserPreferences

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = UserPreferences(application)

    var profileImageUri = mutableStateOf<Uri?>(null)
    var username = mutableStateOf(prefs.username)
    var status = mutableStateOf(prefs.status)
    var isDarkMode = mutableStateOf(prefs.isDarkMode)

    init {
        loadProfileImage()
    }

    private fun loadProfileImage() {
        val uriString = prefs.profileImageUri
        profileImageUri.value = uriString?.let { Uri.parse(it) }
    }

    fun updateProfileImage(uri: Uri?) {
        profileImageUri.value = uri

        if (uri != null) {
            try {
                getApplication<Application>().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Ignoriraj ako već postoji
            }
            prefs.profileImageUri = uri.toString()
        } else {
            prefs.profileImageUri = null
        }
    }

    fun updateUsername(newUsername: String) {
        username.value = newUsername
        prefs.username = newUsername
    }

    fun updateStatus(newStatus: String) {
        status.value = newStatus
        prefs.status = newStatus
    }

    fun updateDarkMode(isDark: Boolean) {
        isDarkMode.value = isDark
        prefs.isDarkMode = isDark
    }

    fun setInitialDarkMode(systemDarkMode: Boolean) {
        if (!prefs.isDarkModeSet()) {
            isDarkMode.value = systemDarkMode
            prefs.isDarkMode = systemDarkMode
        }
    }
}