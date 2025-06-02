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

    private fun getDeviceDirectory(deviceAddress: String): File {
        val deviceDir = File(context.filesDir, deviceAddress)
        if (!deviceDir.exists()) {
            deviceDir.mkdirs()
        }
        return deviceDir
    }

    private fun getMessageFileForDevice(deviceAddress: String): File {
        val safeAddress = deviceAddress.replace(":", "_")
        val deviceDir = getDeviceDirectory(safeAddress)
        return File(deviceDir, "messages.json")
    }


    fun saveMessage(message: BluetoothMessage, deviceAddress: String) {
        val file = getMessageFileForDevice(deviceAddress)
        val messages = loadMessages(deviceAddress).toMutableList()
        messages.add(message)
        val json = gson.toJson(messages)
        file.writeText(json)
    }

    fun loadMessages(deviceAddress: String): List<BluetoothMessage> {
        val file = getMessageFileForDevice(deviceAddress)
        if (!file.exists()) return emptyList()
        val json = file.readText()
        val type = object : TypeToken<List<BluetoothMessage>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveBase64File(base64: String, fileName: String, deviceAddress: String): String {
        val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
        val deviceDir = getDeviceDirectory(deviceAddress)
        //checks if file names are identical
        val extension = fileName.substringAfterLast('.', "")
        val baseName = fileName.substringBeforeLast('.', fileName)
        val uniqueSuffix = System.currentTimeMillis() // or UUID.randomUUID()
        val safeFileName = if (extension.isNotEmpty()) {
            "$baseName-$uniqueSuffix.$extension"
        } else {
            "$baseName-$uniqueSuffix"
        }
        val file = File(deviceDir, fileName)
        file.writeBytes(bytes)
        return file.absolutePath
    }

    fun getAllFilesForDevice(deviceAddress: String): List<File> {
        val deviceDir = getDeviceDirectory(deviceAddress)
        return deviceDir.listFiles()?.toList() ?: emptyList()
    }

    fun saveChatName(deviceAddress: String, name: String) {
        val safeAddress = deviceAddress.replace(":", "_")
        val deviceDir = getDeviceDirectory(safeAddress)
        val nameFile = File(deviceDir, "name.json")
        nameFile.writeText(gson.toJson(name))
    }

    fun getChatName(deviceAddress: String): String? {
        val safeAddress = deviceAddress.replace(":", "_")
        val deviceDir = getDeviceDirectory(safeAddress)
        val nameFile = File(deviceDir, "name.json")
        return if (nameFile.exists()) {
            gson.fromJson(nameFile.readText(), String::class.java)
        } else {
            null
        }
    }


}

