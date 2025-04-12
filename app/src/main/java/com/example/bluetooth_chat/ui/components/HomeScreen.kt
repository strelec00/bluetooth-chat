package com.example.bluetooth_chat.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            )) {
            Text("Go to Bluetooth Devices")
        }
    }
}
