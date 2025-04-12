package com.example.bluetooth_chat.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavbar() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val items = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Chat", Icons.Default.MailOutline),
        NavItem("Groups", Icons.Default.Face),
        NavItem("Profile", Icons.Default.Person)
    )

    NavigationBar(
        modifier = Modifier
            .shadow(
                8.dp,
                ambientColor = Color.Black,
                spotColor = Color.Black
            ),
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.tertiary
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selectedIndex == index)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selectedIndex == index)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector)
