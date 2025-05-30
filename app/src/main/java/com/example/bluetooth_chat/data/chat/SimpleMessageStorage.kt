package com.example.bluetooth_chat.data.chat

import android.content.Context
import com.example.bluetooth_chat.domain.chat.BluetoothMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimpleMessageStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val fileName = "messages.json"

    private fun getFile(): File = File(context.filesDir, fileName)

    fun saveMessage(message: BluetoothMessage) {
        val messages = loadMessages().toMutableList()
        messages.add(message)
        val json = gson.toJson(messages)
        getFile().writeText(json)
    }

    fun loadMessages(): List<BluetoothMessage> {
        val file = getFile()
        if (!file.exists()) return emptyList()
        val json = file.readText()
        val type = object : TypeToken<List<BluetoothMessage>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
