package com.example.bluetooth_chat.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth_chat.domain.chat.BluetoothMessage
import java.io.File
import androidx.core.content.FileProvider
import android.webkit.MimeTypeMap

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (message.isFromLocalUser)
        MaterialTheme.colorScheme.onTertiary
    else
        MaterialTheme.colorScheme.onBackground

    val senderTextColor = MaterialTheme.colorScheme.secondary
    val messageTextColor = MaterialTheme.colorScheme.primary

    val context = LocalContext.current

    Column(
        modifier = modifier
            .background(color = bubbleColor, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
            .widthIn(max = 300.dp)
    ) {
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
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (message.isFile && message.fileName != null) {
            Text(
                text = "File: ${message.fileName}",
                fontSize = 14.sp,
                color = messageTextColor
            )
            if (message.fileSize != null) {
                Text(
                    text = "Size: ${formatFileSize(message.fileSize)}",
                    fontSize = 12.sp,
                    color = messageTextColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    message.localFilePath?.let { filePath ->
                        val file = File(filePath)
                        val fileUri = FileProvider.getUriForFile(
                            context,
                            context.packageName + ".provider",
                            file
                        )
                        val mimeType = getMimeType(file)
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileUri, mimeType)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        try {
                            context.startActivity(Intent.createChooser(intent, "Open file"))
                        } catch (e: Exception) {
                            // Optionally show a Toast: "No app found to open this file"
                        }
                    }
                },
                enabled = message.localFilePath != null
            ) {
                Text("Open File")
            }
        } else {
            Text(
                text = message.message,
                fontSize = 14.sp,
                color = messageTextColor
            )
        }
    }
}

fun getMimeType(file: File): String {
    val extension = file.extension.lowercase()
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        ?: "application/octet-stream"
}


fun formatFileSize(size: Long?): String {
    if (size == null) return ""
    val kb = size / 1024
    val mb = kb / 1024
    return when {
        mb > 0 -> "$mb MB"
        kb > 0 -> "$kb KB"
        else -> "$size B"
    }
}