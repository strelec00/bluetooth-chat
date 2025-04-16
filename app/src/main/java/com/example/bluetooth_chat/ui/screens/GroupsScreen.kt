package com.example.bluetooth_chat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GroupsScreen(modifier: Modifier = Modifier) {
    // Column used for vertical layout of components
    Column(
        modifier = modifier
            .fillMaxSize() // Takes up the full screen size
            .padding(32.dp), // Adds padding around the content
        verticalArrangement = Arrangement.Center, // Centers content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally
    ) {
        // Text displaying a simple message for the Groups screen
        Text(text = "This is the GroupsScreen")
    }
}