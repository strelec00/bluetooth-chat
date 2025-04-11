package com.example.bluetooth_chat.ui.theme

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.bluetooth_chat.BluetoothDevicesActivity

@Composable
fun HomeScreen(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "This is the $name home page!")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            val intent = Intent(context, BluetoothDevicesActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Go to Bluetooth Devices")
        }
    }
}
