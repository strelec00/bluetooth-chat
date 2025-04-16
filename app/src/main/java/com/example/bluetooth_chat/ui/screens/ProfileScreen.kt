package com.example.bluetooth_chat.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluetooth_chat.ui.components.NavbarBack

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    // Scaffold provides a basic layout structure with a top bar
    Scaffold(
        topBar = {
            // Custom NavbarBack component for the top bar, with a back button
            NavbarBack(title = "Profile", onBack = onBack)
        }
    ) { innerPadding ->
        // Content area of the screen
        Column(
            modifier = Modifier
                .fillMaxSize() // Takes up the full screen size
                .padding(innerPadding) // Applies padding from the Scaffold
                .padding(16.dp) // Adds extra padding around the content
        ) {
            // Text displaying a welcome message on the profile screen
            Text("Welcome to your profile!", style = MaterialTheme.typography.headlineMedium)
        }
    }
}