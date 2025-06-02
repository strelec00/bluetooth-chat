package com.example.bluetooth_chat.data.chat


import android.content.Context
import com.example.bluetooth_chat.domain.chat.BluetoothMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object MessageStorage {
    private const val FILENAME = "message_history.json"

    fun saveMessages(context: Context, messages: List<BluetoothMessage>) {
        val json = Gson().toJson(messages)
        val file = File(context.filesDir, FILENAME)
        file.writeText(json)
    }

    fun loadMessages(context: Context): List<BluetoothMessage> {
        val file = File(context.filesDir, FILENAME)
        return if (file.exists()) {
            try {
                val json = file.readText()
                val type = object : TypeToken<List<BluetoothMessage>>() {}.type
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}