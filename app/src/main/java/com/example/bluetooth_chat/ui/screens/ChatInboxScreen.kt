package com.example.bluetooth_chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*


/**
 * Data class to represent a message with text, timestamp, sender, and whether it is sent by the user.
 */
data class Message(
    val text: String,
    val timestamp: String,
    val isSentByUser: Boolean,
    val sender: String
)

/**
 * Composable function for displaying the inbox chat screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInboxScreen() {
    // State to hold the current text input by the user
    var message by remember { mutableStateOf("") }

    // List of messages for the chat
    val messages = remember {
        mutableStateListOf(
            Message("Hey Ethan, just checking in on our project progress.", "9:15 AM", false, "Sophie Williams"),
            Message("Hey Sophie, thanks for reaching out. I'm finishing up some details, will be available shortly.", "9:20 AM", true, "Ethan Harper"),
            Message("Got it. Let me know when you're ready.", "9:35 AM", false, "Sophie Williams"),
            Message("Almost done, about 30 more minutes. Works for you?", "9:45 AM", true, "Ethan Harper")
        )
    }

    // State to remember the scroll position in the chat
    val listState = rememberLazyListState()

    /**
     * Function to get the current timestamp in the format "h:mm a".
     */
    fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Automatically scroll to the bottom when the messages list changes
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    // Scaffold to manage top bar, bottom bar, and body content of the screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sophie Williams") },
                navigationIcon = {
                    // Back button
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") // Updated to AutoMirrored
                    }

                },
                actions = {
                    // Profile button
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = {
            // Bottom bar to type and send messages
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TextField for entering messages
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Type your message") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color(0xFFE0E0E0)
                    )
                )
                // Send button
                IconButton(onClick = {
                    if (message.isNotBlank()) {
                        // Add the user's message to the list
                        messages.add(
                            Message(message, getCurrentTimestamp(), true, "Ethan Harper")
                        )
                        // Add a reply message for simulation
                        messages.add(
                            Message("Got it, thanks!", getCurrentTimestamp(), false, "Sophie Williams")
                        )
                        // Clear the input field after sending
                        message = ""
                    }
                }) {
                    Icon(Icons.Filled.Send, contentDescription = "Send")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Header showing user profile and initial chat message
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Display avatar
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Display name and a description of the chat
                Text("Sophie Williams", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "This is the start of your chat with Sophie Williams.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Spacer to separate sections
            Spacer(modifier = Modifier.height(16.dp))
            Text("Today", fontWeight = FontWeight.SemiBold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            // LazyColumn to display the list of messages in the chat
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Iterate over messages and display each in a styled bubble
                items(messages) { msg ->
                    MessageBubbleStyled(message = msg)
                }
            }
        }
    }
}

/**
 * Composable to display a message bubble with text and timestamp.
 */
@Composable
fun MessageBubbleStyled(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar shown for all messages
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Avatar",
            tint = Color.Gray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .widthIn(max = 300.dp)
        ) {
            // Message header: sender name and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = message.sender,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = message.timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Message content
            Text(
                text = message.text,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Preview function for displaying the chat inbox screen during development.
 */
@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatInboxScreen()
}
