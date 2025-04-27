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
import androidx.compose.runtime.Composable

data class User(val name: String, val profilePicRes: Int)

// Screen to list users message history
@Composable
fun ChatScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") } // Holds the current search query

    // Dummy list of users with names and profile picture resources
    val dummyUsers = listOf(
        User("Alice Johnson", R.drawable.profile1),
        User("Bob Smith", R.drawable.profile2),
        User("Big Mike", R.drawable.profile3),
        User("John Johnson", R.drawable.profile4),
        User("Michael Smith", R.drawable.profile5),
        User("Frank", R.drawable.profile1),
        User("Jerry", R.drawable.profile2),
        User("Steven Seagull", R.drawable.profile3),
    )

    // Filter users based on search query
    val filteredUsers = remember(searchQuery) {
        if (searchQuery.isBlank()) dummyUsers
        else dummyUsers.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Custom text selection colors for text field
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
    )

    // Main content of the screen
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search text field for filtering users
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it }, // Update the search query
                label = { Text("Search people") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Adds padding below the text field
                singleLine = true, // Ensures the text field is a single line
                colors = OutlinedTextFieldDefaults.colors( // Custom colors for the text field
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

        // Lazy column to display the filtered list of users
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredUsers) { user -> // Iterate over filtered users
                Surface(
                    modifier = Modifier
                        .fillMaxWidth() // Take up full width of screen
                        .height(64.dp) // Fixed height for each item
                        .clickable { /* handle user click */ }, // Add a clickable action for each item
                    shape = MaterialTheme.shapes.medium, // Rounded corners for the item
                    tonalElevation = 2.dp, // Elevation for shadow effect
                    color = MaterialTheme.colorScheme.surfaceVariant // Background color for each item
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Center contents vertically
                        modifier = Modifier
                            .fillMaxSize() // Take up full size of the row
                            .padding(horizontal = 16.dp) // Horizontal padding for content inside the row
                    ) {
                        // Profile picture of the user (circular shape)
                        Image(
                            painter = painterResource(id = user.profilePicRes),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop, // Crop image to fit inside the circle
                            modifier = Modifier
                                .size(40.dp) // Set image size
                                .clip(CircleShape) // Clip image into a circular shape
                                .padding(end = 0.dp)
                        )
                        // User's name displayed next to the profile picture
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium, // Text style for name
                            color = MaterialTheme.colorScheme.onSurface, // Text color
                            modifier = Modifier.padding(start = 8.dp) // Add space between image and text
                        )
                    }
                }
            }
        }
    }
}