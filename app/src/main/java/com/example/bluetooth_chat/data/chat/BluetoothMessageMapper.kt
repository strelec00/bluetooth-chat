package com.example.bluetooth_chat.data.chat

import com.example.bluetooth_chat.domain.chat.BluetoothMessage

fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
    // File message: "FILE:<filename>:<base64>"
    if (startsWith("FILE:")) {
        val firstColon = indexOf(':', startIndex = 5)
        if (firstColon > 5) {
            val fileName = substring(5, firstColon)
            val base64 = substring(firstColon + 1)
            return BluetoothMessage(
                message = base64,
                senderName = "", // You can include sender info if you want
                isFromLocalUser = isFromLocalUser,
                isFile = true,
                fileName = fileName
            )
        }
    }
    // Regular message: "sender#message"
    val name = substringBeforeLast("#")
    val message = substringAfter("#")
    return BluetoothMessage(
        message = message,
        senderName = name,
        isFromLocalUser = isFromLocalUser
    )
}

fun BluetoothMessage.toByteArray(): ByteArray {
    return if (isFile && fileName != null) {
        // Send as "FILE:<filename>:<base64>"
        "FILE:$fileName:$message".encodeToByteArray()
    } else {
        "$senderName#$message".encodeToByteArray()
    }
}
