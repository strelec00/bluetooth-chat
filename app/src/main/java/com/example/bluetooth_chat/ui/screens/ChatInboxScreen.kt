package com.example.bluetooth_chat.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import com.example.bluetooth_chat.ui.components.NavbarBack
import com.example.bluetooth_chat.ui.theme.BluetoothchatTheme
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
 * Composable function for displaying the inbox chat screen with a custom NavbarBack.
 * @param userName The name of the chat recipient to display in the top bar.
 * @param onBack Callback when back arrow is pressed.
 */
@Composable
fun ChatInboxScreen(
    userName: String,
    onBack: () -> Unit = {}
) {
    var message by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message("Hey $userName here, just checking in on our project progress.", "9:15 AM", false, userName),
            Message("Got it, thanks for reaching out!", "9:20 AM", true, "Me"),
            Message("Let me know when you're ready.", "9:35 AM", false, userName),
            Message("Sure thing, chat with you soon!", "9:45 AM", true, "Me")
        )
    }
    val listState = rememberLazyListState()

    fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(Date())
    }

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    Scaffold(
        topBar = {
            NavbarBack(
                title = userName,
                onBack = onBack
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                IconButton(onClick = {
                    if (message.isNotBlank()) {
                        messages.add(Message(message, getCurrentTimestamp(), true, "Me"))
                        messages.add(Message("Ok, got it!", getCurrentTimestamp(), false, userName))
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
            // Avatar below nav bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(100.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This is your chat with",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Today", fontWeight = FontWeight.SemiBold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    MessageBubbleStyled(message = msg)
                }
            }
        }
    }
}

@Composable
fun MessageBubbleStyled(message: Message) {
    val bubbleColor = if (message.isSentByUser) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onBackground
    val arrangement = if (message.isSentByUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isSentByUser) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                tint = Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .background(color = bubbleColor, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(max = 300.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = message.sender, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                Text(text = message.timestamp, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = message.text, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        }

        if (message.isSentByUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                tint = Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

