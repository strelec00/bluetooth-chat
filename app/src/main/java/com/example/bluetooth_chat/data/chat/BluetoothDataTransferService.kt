package com.example.bluetooth_chat.data.chat

import android.bluetooth.BluetoothSocket
import com.example.bluetooth_chat.domain.chat.BluetoothMessage
import com.example.bluetooth_chat.domain.chat.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected) {
                return@flow
            }

            val buffer = ByteArray(4096) // Increase buffer for larger file transfers

            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    throw TransferFailedException()
                }

                // Read the encrypted message as a string
                val encryptedMessage = buffer.decodeToString(endIndex = byteCount)

                // Decrypt the message before mapping to BluetoothMessage
                val decryptedMessage = try {
                    AesCipher.decrypt(encryptedMessage)
                } catch (e: Exception) {
                    // If decryption fails, fallback to the raw message
                    encryptedMessage
                }

                emit(
                    decryptedMessage.toBluetoothMessage(
                        isFromLocalUser = false
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
            } catch(e: IOException) {
                e.printStackTrace()
                return@withContext false
            }

            true
        }
    }
}
