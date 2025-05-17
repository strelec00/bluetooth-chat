
package com.example.bluetooth_chat.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bluetooth_chat.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.navigation.NavHostController

// Data class for user

data class User(val name: String, val profilePicRes: Int)

/**
 * ChatScreen lists users. When a user is selected it shows ChatInboxScreen inline,
 * and notifies parent via onDetailOpen whether detail view is active.
 */
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onDetailOpen: (Boolean) -> Unit,
    navController: NavHostController
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    // Dummy users list
    val dummyUsers = listOf(
        User("Alice Johnson", R.drawable.profile1),
        User("Bob Smith", R.drawable.profile2),
        User("Big Mike", R.drawable.profile3),
        User("John Johnson", R.drawable.profile4),
        User("Michael Smith", R.drawable.profile5),
        User("Frank", R.drawable.profile1),
        User("Jerry", R.drawable.profile2),
        User("Steven Seagull", R.drawable.profile3)
    )

    // Filter logic
    val filteredUsers = remember(searchQuery) {
        if (searchQuery.isBlank()) dummyUsers
        else dummyUsers.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (selectedUser == null) {
            // notify parent that detail is closed
            LaunchedEffect(Unit) { onDetailOpen(false) }

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search people") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredUsers) { user ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable {
                                selectedUser = user
                            },
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = user.profilePicRes),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // notify parent detail is open
            LaunchedEffect(selectedUser) { onDetailOpen(true) }

            // Show inbox for selected user
            ChatInboxScreen(
                userName = selectedUser!!.name,
                onBack = {
                    selectedUser = null
                    onDetailOpen(false)
                }
            )
        }
    }
}
