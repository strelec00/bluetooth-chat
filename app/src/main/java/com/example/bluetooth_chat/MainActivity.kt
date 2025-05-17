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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluetooth_chat.bluetooth.BluetoothChecker
import com.example.bluetooth_chat.ui.components.BottomNavbar
import com.example.bluetooth_chat.ui.components.Navbar
import com.example.bluetooth_chat.ui.navigation.Screen
import com.example.bluetooth_chat.ui.screens.EnableBluetoothScreen
import com.example.bluetooth_chat.ui.screens.GroupsScreen
import com.example.bluetooth_chat.ui.screens.HomeScreen
import com.example.bluetooth_chat.ui.screens.ProfileScreen
import com.example.bluetooth_chat.ui.screens.ChatScreen
import com.example.bluetooth_chat.ui.screens.InfoScreen
import com.example.bluetooth_chat.ui.screens.BluetoothDevicesScreen
import com.example.bluetooth_chat.ui.theme.BluetoothchatTheme
import com.example.bluetooth_chat.util.AppPreferences

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

        AppPreferences.resetFirstLaunch(applicationContext)

        setContent {
            val context = applicationContext
            var showInfoScreen by remember { mutableStateOf(AppPreferences.isFirstLaunch(context)) }
            val navController = rememberNavController()
            var isDetailOpen by remember { mutableStateOf(false) }
            val currentDestination by navController.currentBackStackEntryAsState()

            val showBars = !isDetailOpen && when (currentDestination?.destination?.route) {
                Screen.Home.route,
                Screen.Chat.route,
                Screen.Groups.route,
                Screen.BluetoothDevices.route -> true
                else -> false
            }

            BluetoothchatTheme {
                if (showInfoScreen) {
                    InfoScreen(onFinish = {
                        AppPreferences.setFirstLaunchComplete(context)
                        showInfoScreen = false
                    })
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (showBars) {
                                Navbar(title = "BluetoothChat") {
                                    navController.navigate(Screen.Profile.route)
                                }
                            }
                        },
                        bottomBar = {
                            if (showBars) {
                                BottomNavbar(navController = navController)
                            }
                        }
                    ) { innerPadding ->
                        BluetoothChecker(
                            contentWhenOn = {
                                NavHost(
                                    navController = navController,
                                    startDestination = Screen.Home.route,
                                    modifier = Modifier.padding(innerPadding)
                                ) {
                                    composable(Screen.Home.route) {
                                        HomeScreen(name = "BluetoothChat")
                                        isDetailOpen = false
                                    }
                                    composable(Screen.Chat.route) {
                                        ChatScreen(
                                            navController = navController,
                                            onDetailOpen = { isDetailOpen = it }
                                        )
                                    }
                                    composable(Screen.Groups.route) {
                                        GroupsScreen()
                                        isDetailOpen = false
                                    }
                                    composable(Screen.BluetoothDevices.route) {
                                        BluetoothDevicesScreen()
                                        isDetailOpen = false
                                    }
                                    composable(Screen.Profile.route) {
                                        ProfileScreen(onBack = { navController.popBackStack() })
                                        isDetailOpen = false
                                    }
                                }
                            },
                            contentWhenOff = {
                                NavHost(
                                    navController = navController,
                                    startDestination = Screen.Home.route,
                                    modifier = Modifier.padding(innerPadding)
                                ) {
                                    composable(Screen.Home.route) {
                                        EnableBluetoothScreen()
                                        isDetailOpen = false
                                    }
                                    composable(Screen.Chat.route) {
                                        ChatScreen(
                                            navController = navController,
                                            onDetailOpen = { isDetailOpen = it }
                                        )
                                    }
                                    composable(Screen.Groups.route) {
                                        GroupsScreen()
                                        isDetailOpen = false
                                    }
                                    composable(Screen.BluetoothDevices.route) {
                                        EnableBluetoothScreen()
                                        isDetailOpen = false
                                    }
                                    composable(Screen.Profile.route) {
                                        ProfileScreen(onBack = { navController.popBackStack() })
                                        isDetailOpen = false
                                    }
                                }
                            }
                        )
                    }
                }
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
