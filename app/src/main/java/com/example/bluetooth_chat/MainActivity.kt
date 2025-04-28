package com.example.bluetooth_chat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.*
import com.example.bluetooth_chat.bluetooth.BluetoothChecker
import com.example.bluetooth_chat.ui.components.*
import com.example.bluetooth_chat.ui.navigation.Screen
import com.example.bluetooth_chat.ui.screens.BluetoothDevicesScreen
import com.example.bluetooth_chat.ui.screens.ChatScreen
import com.example.bluetooth_chat.ui.screens.EnableBluetoothScreen
import com.example.bluetooth_chat.ui.screens.GroupsScreen
import com.example.bluetooth_chat.ui.screens.HomeScreen
import com.example.bluetooth_chat.ui.screens.ProfileScreen
import com.example.bluetooth_chat.ui.theme.BluetoothchatTheme

class MainActivity : ComponentActivity() {

    // Launcher for requesting a single permission (Bluetooth)
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // Show splash screen
        enableEdgeToEdge() // Use full screen layout

        // Register the permission request callback
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Bluetooth permission is required", Toast.LENGTH_SHORT).show()
            }
        }

        // Request permission if not already granted
        if (!hasBluetoothConnectPermission()) {
            requestBluetoothPermissionIfNeeded()
        }

        setContent {
            val navController = rememberNavController()
            val currentDestination by navController.currentBackStackEntryAsState()

            // Determine if the current screen should show the top/bottom bars
            val showBars = when (currentDestination?.destination?.route) {
                Screen.Home.route,
                Screen.Chat.route,
                Screen.Groups.route,
                Screen.BluetoothDevices.route -> true

                else -> false
            }

            BluetoothchatTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        // Show top bar only for selected screens
                        if (showBars) {
                            Navbar(title = "BluetoothChat") {
                                navController.navigate(Screen.Profile.route)
                            }
                        }
                    },
                    bottomBar = {
                        // Show bottom bar only for selected screens
                        if (showBars) {
                            BottomNavbar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    BluetoothChecker(
                        contentWhenOn = {
                            // Navigation graph when Bluetooth is ON
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Home.route,
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable(Screen.Home.route) {
                                    HomeScreen(name = "BluetoothChat")
                                }
                                composable(Screen.Chat.route) {
                                    ChatScreen()
                                }
                                composable(Screen.Groups.route) {
                                    GroupsScreen()
                                }
                                composable(Screen.BluetoothDevices.route) {
                                    BluetoothDevicesScreen()
                                }
                                composable(Screen.Profile.route) {
                                    ProfileScreen(
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        },
                        contentWhenOff = {
                            // Navigation graph when Bluetooth is OFF
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Home.route,
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable(Screen.Home.route) {
                                    EnableBluetoothScreen()
                                }
                                composable(Screen.Chat.route) {
                                    ChatScreen()
                                }
                                composable(Screen.Groups.route) {
                                    GroupsScreen()
                                }
                                composable(Screen.BluetoothDevices.route) {
                                    EnableBluetoothScreen()
                                }
                                composable(Screen.Profile.route) {
                                    ProfileScreen(
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Checks if BLUETOOTH_CONNECT permission is granted (only for Android 12+)
    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required on older versions
        }
    }

    // Requests Bluetooth permission if needed (only for Android 12+)
    private fun requestBluetoothPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
}