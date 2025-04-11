package com.example.bluetooth_chat

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluetooth_chat.ui.theme.BluetoothchatTheme
import com.example.bluetooth_chat.ui.components.Navbar

class BluetoothDevicesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setContent {
            BluetoothchatTheme {
                val dummyDevices = listOf(
                    "Device A - 12:34:56:78:90",
                    "Device B - 98:76:54:32:10",
                    "Device C - AA:BB:CC:DD:EE",
                    "Device D - 01:23:45:67:89"
                )

                Scaffold(
                    topBar = { Navbar(title = "Nearby Devices") }
                ) { innerPadding ->
                    BluetoothDevicesScreen(
                        devices = dummyDevices,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BluetoothDevicesScreen(devices: List<String>, modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredDevices = remember(searchQuery, devices) {
        if (searchQuery.isBlank()) devices
        else devices.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search devices") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        Text(
            text = "Available Devices",
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn {
            items(filteredDevices) { device ->
                Text(
                    text = device,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}
