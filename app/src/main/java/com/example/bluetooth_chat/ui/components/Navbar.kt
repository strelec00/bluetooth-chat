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

// TOP Navbar component
@Composable
fun Navbar(title: String, function: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow( // Add shadow below navbar
                elevation = 8.dp,
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile icon *button on the left*
            IconButton(
                onClick = function,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp
            )

            // Settings icon *button on the right*
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