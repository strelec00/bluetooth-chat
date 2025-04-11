package com.example.bluetooth_chat

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth_chat.ui.theme.BluetoothchatTheme

class BluetoothDevicesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Remove the default activity title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setContent {
            BluetoothchatTheme {
                val dummyDevices = listOf(
                    "Device A - 12:34:56:78:90",
                    "Device B - 98:76:54:32:10",
                    "Device C - AA:BB:CC:DD:EE"
                )

                // The custom title bar uses theme colors from your custom color scheme.
                Scaffold(
                    topBar = { CustomTitleBar(title = "Nearby Devices") }
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
fun CustomTitleBar(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp
        )
    }
}

@Composable
fun BluetoothDevicesScreen(devices: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Available Devices",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(devices) { device ->
                Text(
                    text = device,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
