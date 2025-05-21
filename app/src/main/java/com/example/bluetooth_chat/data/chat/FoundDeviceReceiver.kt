package com.plcoding.bluetoothchat.data.chat

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class FoundDeviceReceiver(
    private val onDeviceFound: (BluetoothDevice) -> Unit
): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        android.util.Log.d("Bluetooth", "onReceive called with action: ${intent?.action}")
        android.util.Log.d("FoundDeviceReceiver", "Received unexpected intent action")
        when(intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                android.util.Log.d("FoundDeviceReceiver", "ACTION_FOUND received!")
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    android.util.Log.d("FoundDeviceReceiver", "Unexpected intent: ${intent?.action}")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                device?.let(onDeviceFound)
            }
        }
    }
}