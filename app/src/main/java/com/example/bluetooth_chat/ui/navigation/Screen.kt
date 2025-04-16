package com.example.bluetooth_chat.ui.navigation

// Sealed class representing all screens/routes in the app
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Chat : Screen("chat")
    data object Groups : Screen("groups")
    data object BluetoothDevices : Screen("bluetooth_devices")
    data object Profile : Screen("profile")
}
