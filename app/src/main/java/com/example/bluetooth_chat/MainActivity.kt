package com.example.bluetooth_chat

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.bluetooth_chat.ui.viewmodel.AppPreferences
import com.example.bluetooth_chat.presentation.BluetoothViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bluetooth_chat.ui.screens.ChatInboxScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide() // hides the defalut bar at the top

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if(canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }

        AppPreferences.resetFirstLaunch(applicationContext)

        setContent {
            val context = applicationContext
            var showInfoScreen by remember { mutableStateOf(AppPreferences.isFirstLaunch(context)) }
            val navController = rememberNavController()
            var isDetailOpen by remember { mutableStateOf(false) }
            val currentDestination by navController.currentBackStackEntryAsState()
            val viewModel = hiltViewModel<BluetoothViewModel>()
            val state by viewModel.state.collectAsState()

            val showBars = !isDetailOpen && when (currentDestination?.destination?.route) {
                Screen.Home.route,
                Screen.Chat.route,
                Screen.Groups.route,
                Screen.BluetoothDevices.route -> true
                else -> false
            }

            BluetoothchatTheme {
                LaunchedEffect(key1 = state.errorMessage) {
                    state.errorMessage?.let { message ->
                        Toast.makeText(
                            applicationContext,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                LaunchedEffect(key1 = state.isConnected) {
                    if(state.isConnected) {
                        Toast.makeText(
                            applicationContext,
                            "You're connected!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

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
                                            onDetailOpen = { isDetailOpen = it },
                                            onStartServer = viewModel::waitForIncomingConnections
                                        )
                                    }
                                    composable(Screen.Groups.route) {
                                        GroupsScreen()
                                        isDetailOpen = false
                                    }
                                    composable(Screen.BluetoothDevices.route) {
                                        isDetailOpen = false

                                        when {
                                            state.isConnecting -> {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    CircularProgressIndicator()
                                                    Text(text = "Connecting...")
                                                }
                                            }

                                            state.isConnected -> {
                                                ChatInboxScreen(
                                                    state = state,
                                                    onDisconnect = viewModel::disconnectFromDevice,
                                                    onSendMessage = viewModel::sendMessage,
                                                    onSendFile = { fileName, base64 -> viewModel.sendFile(fileName, base64) }
                                                )
                                            }

                                            else -> {
                                                BluetoothDevicesScreen(
                                                    state = state,
                                                    onStartScan = viewModel::startScan,
                                                    onStopScan = viewModel::stopScan,
                                                    onDeviceClick = viewModel::connectToDevice,
                                                    onStartServer = viewModel::waitForIncomingConnections
                                                )
                                            }
                                        }
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
                                            onDetailOpen = { isDetailOpen = it },
                                            onStartServer = viewModel::waitForIncomingConnections
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
}
