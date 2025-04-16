package com.example.bluetooth_chat.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.bluetooth_chat.ui.theme.Typography
import androidx.compose.ui.text.style.TextAlign

@Composable
fun EnableBluetoothScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current // Get the current context

    Box(
        modifier = modifier
            .fillMaxSize(), // Fill the available screen size
        contentAlignment = Alignment.Center // Center content within the box
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Title text
            Text(
                text = "Bluetooth is turned off",
                style = TextStyle(
                    fontFamily = Typography.bodyLarge.fontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp)) // Spacing between texts

            // Description text
            Text(
                text = "Enable Bluetooth to start connecting with nearby devices for chat.",
                style = TextStyle(
                    fontFamily = Typography.bodyLarge.fontFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center // Center-align the text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp)) // Spacing before button

            // Button to open Bluetooth settings
            Button(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) // Launch system Bluetooth settings
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Enable Bluetooth")
            }
        }
    }
}