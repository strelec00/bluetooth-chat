package com.example.bluetooth_chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth_chat.domain.chat.BluetoothMessage

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {
    // Bubble background color matching first file's style
    val bubbleColor = if (message.isFromLocalUser)
        MaterialTheme.colorScheme.onTertiary
    else
        MaterialTheme.colorScheme.onBackground

    // Text colors to match first file (primary for message text, secondary for sender)
    val senderTextColor = MaterialTheme.colorScheme.secondary
    val messageTextColor = MaterialTheme.colorScheme.primary

    // Alignment based on sender can be handled in the parent LazyColumn or here by applying padding/margin

    Column(
        modifier = modifier
            .background(color = bubbleColor, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
            .widthIn(max = 300.dp)
    ) {
        // Sender name styled like the first file
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message.senderName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = senderTextColor
            )
            // If you have a timestamp in BluetoothMessage, add it here, e.g.:
            // Text(text = message.timestamp, fontSize = 12.sp, color = senderTextColor)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Message text styled like the first file
        Text(
            text = message.message,
            fontSize = 14.sp,
            color = messageTextColor
        )
    }
}
