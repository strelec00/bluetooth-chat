package com.example.bluetooth_chat.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    var profileImageUri = mutableStateOf<Uri?>(null)
    var username = mutableStateOf("Sophia")
    var about = mutableStateOf("Available to chat")
    var isDarkMode = mutableStateOf(false)
}
