package com.example.bluetooth_chat.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun BluetoothChecker(
    contentWhenOn: @Composable () -> Unit,
    contentWhenOff: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Get BluetoothAdapter from system service
    val bluetoothAdapter = remember {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        bluetoothManager.adapter
    }

    // Track current Bluetooth state
    var isBluetoothOn by remember {
        mutableStateOf(bluetoothAdapter?.isEnabled == true)
    }

    // Register receiver to listen for Bluetooth state changes
    DisposableEffect(Unit) {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val state =
                    intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                isBluetoothOn = state == BluetoothAdapter.STATE_ON
            }
        }
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    if (isBluetoothOn) {
        contentWhenOn()
    } else {
        contentWhenOff()
    }
}