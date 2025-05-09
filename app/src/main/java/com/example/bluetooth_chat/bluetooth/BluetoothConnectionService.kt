package com.example.bluetooth_chat.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothConnectionService(private val context: Context) {

    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun connectToDevice(
        device: BluetoothDevice,
        onConnected: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                    Log.e("BluetoothService", "Missing BLUETOOTH_CONNECT permission")
                    onError("Missing Bluetooth permission")
                    return@launch
                }

                socket = device.createRfcommSocketToServiceRecord(uuid)
                socket?.connect()

                input = socket?.inputStream
                output = socket?.outputStream

                Log.d("BluetoothService", "Connected to ${device.name}")
                onConnected()
            } catch (e: Exception) {
                Log.e("BluetoothService", "Connection failed", e)
                onError(e.message ?: "Unknown error")
                close()
            }
        }
    }

    fun send(data: ByteArray) {
        try {
            output?.write(data)
            output?.flush()
            Log.d("BluetoothService", "Sent ${data.size} bytes")
        } catch (e: Exception) {
            Log.e("BluetoothService", "Send failed", e)
        }
    }

    fun receive(onData: (ByteArray) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            try {
                while (true) {
                    val bytes = input?.read(buffer) ?: break
                    if (bytes > 0) {
                        val data = buffer.copyOf(bytes)
                        onData(data)
                    }
                }
            } catch (e: Exception) {
                Log.e("BluetoothService", "Receive failed", e)
            }
        }
    }

    fun close() {
        input?.close()
        output?.close()
        socket?.close()
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
