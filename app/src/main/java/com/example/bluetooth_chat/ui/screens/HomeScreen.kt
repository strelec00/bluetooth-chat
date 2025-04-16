package com.example.bluetooth_chat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(name: String, modifier: Modifier = Modifier) {
    // Column used for vertical layout of components
    Column(
        modifier = modifier
            .fillMaxSize() // Takes up the full screen size
            .padding(32.dp), // Adds padding around the content
        verticalArrangement = Arrangement.Center, // Centers content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally
    ) {
        // Text displaying a message with the passed 'name' value
        Text(text = "This is the $name home page!")

        // Spacer to add space between the text and any subsequent content
        Spacer(modifier = Modifier.height(24.dp))
    }
}