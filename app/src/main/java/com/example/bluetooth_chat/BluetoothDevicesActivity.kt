package com.example.bluetooth_chat

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bluetooth_chat.ui.components.Navbar
import com.example.bluetooth_chat.ui.components.BottomNavbar
import com.example.bluetooth_chat.ui.theme.BluetoothchatTheme

class BluetoothDevicesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setContent {
            BluetoothchatTheme {
                val dummyDevices = listOf(
                    "Sam's iPhone",
                    "Galaxy S10",
                    "Emily's AirPods",
                    "MacBook Pro",
                    "Pixel 6a",
                    "OnePlus Nord",
                    "Ford SYNC",
                    "Bose SoundLink",
                    "Xbox Controller",
                    "JBL Flip 5",
                    "Anya’s iPad",
                    "Tom’s Laptop",
                    "Beats Studio",
                    "Lenovo Tablet"
                )

                Scaffold(
                    topBar = { Navbar(title = "Nearby Devices") },
                    bottomBar = { BottomNavbar() }
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

    val isDarkTheme = isSystemInDarkTheme()
    val bluetoothIcon = if (isDarkTheme) R.drawable.bluetooth_icon_white else R.drawable.bluetooth_icon_black

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search for Bluetooth devices") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Text(
            text = "Available Devices",
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredDevices) { device ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable {},
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = bluetoothIcon),
                            contentDescription = "Bluetooth Icon",
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 12.dp)
                        )
                        Text(
                            text = device,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
