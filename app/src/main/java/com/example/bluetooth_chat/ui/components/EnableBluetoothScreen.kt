package com.example.bluetooth_chat.ui.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun EnableBluetoothScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = { Navbar(title = "BluetoothChat") },
        bottomBar = { BottomNavbar() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Please enable Bluetooth to continue.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                }) {
                    Text("Turn On Bluetooth")
                }
            }
        }
    }
}
