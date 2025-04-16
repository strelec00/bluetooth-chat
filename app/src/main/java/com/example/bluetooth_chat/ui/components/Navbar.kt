package com.example.bluetooth_chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth_chat.R

@Composable
fun Navbar(title: String, function: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth() // Take full screen width
            .height(80.dp) // Fixed height for the navbar
            .shadow( // Add shadow below navbar
                elevation = 8.dp,
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(MaterialTheme.colorScheme.primary) // Set background color from theme
            .statusBarsPadding(), // Add padding for status bar
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween, // Space between profile, title and settings
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile icon button on the left
            IconButton(
                onClick = function, // Trigger function (e.g. navigate to profile screen)
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onPrimary // Icon tint based on theme
                )
            }

            // Title in the center
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp
            )

            // Settings icon button on the right (placeholder)
            IconButton(
                onClick = { /* TODO: Implement settings screen */ },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}