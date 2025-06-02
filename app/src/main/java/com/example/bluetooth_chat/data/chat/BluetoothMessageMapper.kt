package com.example.bluetooth_chat.data.chat

import com.example.bluetooth_chat.domain.chat.BluetoothMessage

fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
    // File message: "FILE:<senderName>:<fileName>:<base64>"
    if (startsWith("FILE:")) {
        val parts = split(":", limit = 4)
        if (parts.size == 4) {
            val senderName = parts[1]
            val fileName = parts[2]
            val base64 = parts[3]
            return BluetoothMessage(
                message = base64,
                senderName = senderName,
                isFromLocalUser = isFromLocalUser,
                isFile = true,
                fileName = fileName
            )
        }
    }
    // Regular message: "senderName#message"
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
        // Include sender name: "FILE:<senderName>:<fileName>:<base64>"
        "FILE:$senderName:$fileName:$message".encodeToByteArray()
    } else {
        // "senderName#message"
        "$senderName#$message".encodeToByteArray()
    }
}

fun BluetoothMessage.toTransferString(): String {
    return if (isFile && fileName != null) {
        "FILE:$senderName:$fileName:$message"
    } else {
        "$senderName#$message"
    }
}
