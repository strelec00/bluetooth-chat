package com.example.bluetooth_chat.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bluetooth_chat.ui.navigation.Screen

// BOTTOM Navbar component
@Composable
fun BottomNavbar(navController: NavController) {
    // List of navigation items in the bottom bar
    val items = listOf(
        NavItem("Home", Icons.Default.Home, Screen.Home.route),
        NavItem("Chat", Icons.Default.MailOutline, Screen.Chat.route),
        NavItem("Groups", Icons.Default.Face, Screen.Groups.route),
        NavItem("Add Device", Icons.Default.Create, Screen.BluetoothDevices.route)
    )

    // Observes current navigation route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier
            .shadow(
                8.dp,
                ambientColor = Color.Black,
                spotColor = Color.Black
            ),
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.tertiary // Uses theme color for background
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route, // Highlights selected item
                onClick = {
                    if (currentRoute != item.route) {
                        // Navigate to selected screen with state restoration
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label, // Important for accessibility
                        tint = if (currentRoute == item.route)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f) // Faded tint for unselected icons
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (currentRoute == item.route)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f) // Faded label for unselected
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

// Simple data class representing a bottom nav item
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)
