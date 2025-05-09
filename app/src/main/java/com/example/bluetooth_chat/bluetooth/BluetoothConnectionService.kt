package com.example.bluetooth_chat.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
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

    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    fun connectToDevice(
        device: BluetoothDevice,
        onConnected: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                    onError("Missing BLUETOOTH_CONNECT permission")
                    return@launch
                }

                val sock = device.createRfcommSocketToServiceRecord(uuid)
                sock.connect()

                socket = sock
                input = sock.inputStream
                output = sock.outputStream

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

    fun receive(callback: (ByteArray) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            try {
                while (true) {
                    val bytesRead = input?.read(buffer) ?: break
                    if (bytesRead > 0) {
                        val received = buffer.copyOf(bytesRead)
                        callback(received)
                    }
                }
            } catch (e: Exception) {
                Log.e("BluetoothService", "Receive failed", e)
            }
        }
    }

    fun close() {
        try {
            input?.close()
            output?.close()
            socket?.close()
            Log.d("BluetoothService", "Connection closed")
        } catch (e: Exception) {
            Log.e("BluetoothService", "Close failed", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): Set<BluetoothDevice> {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return emptySet()
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return adapter?.bondedDevices ?: emptySet()
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
