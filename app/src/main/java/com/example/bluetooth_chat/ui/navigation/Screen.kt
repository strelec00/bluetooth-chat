package com.example.bluetooth_chat.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Groups : Screen("groups")
    object BluetoothDevices : Screen("bluetooth_devices")
}
