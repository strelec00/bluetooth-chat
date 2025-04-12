package com.example.bluetooth_chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bluetooth_chat.ui.components.Navbar
import com.example.bluetooth_chat.ui.components.BottomNavbar
import com.example.bluetooth_chat.ui.theme.BluetoothchatTheme
import com.example.bluetooth_chat.ui.theme.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            BluetoothchatTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Navbar(title = "BluetoothChat")
                    },
                    bottomBar = {
                        BottomNavbar()
                    }
                ) { innerPadding ->
                    HomeScreen(
                        name = "BluetoothChat",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BluetoothchatTheme {
        Scaffold(
            topBar = { Navbar(title = "BluetoothChat") },
            bottomBar = { BottomNavbar() }
        ) {
            HomeScreen(name = "BluetoothChat", modifier = Modifier.padding(it))
        }
    }
}
