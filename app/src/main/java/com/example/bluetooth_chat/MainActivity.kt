package com.example.bluetooth_chat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bluetooth_chat.ui.components.BottomNavbar
import com.example.bluetooth_chat.ui.components.Navbar
import com.example.bluetooth_chat.ui.theme.BluetoothchatTheme
import com.example.bluetooth_chat.ui.components.HomeScreen
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bluetooth_chat.ui.components.EnableBluetoothScreen
import com.example.bluetooth_chat.bluetooth.BluetoothChecker

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Bluetooth permission is required", Toast.LENGTH_SHORT).show()
            }
        }

        if (!hasBluetoothConnectPermission()) {
            requestBluetoothPermissionIfNeeded()
        }

        setContent {
            BluetoothchatTheme {
                BluetoothChecker(
                    contentWhenOn = {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = { Navbar(title = "BluetoothChat") },
                            bottomBar = { BottomNavbar() }
                        ) { innerPadding ->
                            HomeScreen(
                                name = "BluetoothChat",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    },
                    contentWhenOff = {
                        EnableBluetoothScreen()
                    }
                )
            }
        }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestBluetoothPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
}