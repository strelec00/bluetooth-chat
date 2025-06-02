package com.example.bluetooth_chat.data.chat

import android.bluetooth.BluetoothSocket
import com.example.bluetooth_chat.domain.chat.BluetoothMessage
import com.example.bluetooth_chat.domain.chat.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {

    // --- NEW: Write with length prefix ---
    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val output = DataOutputStream(socket.outputStream)
                output.writeInt(bytes.size) // 4-byte length prefix (big-endian)
                output.write(bytes)
                output.flush()
                true
            } catch(e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    // --- NEW: Read with length prefix and buffer reassembly ---
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected) return@flow

            val input = DataInputStream(socket.inputStream)

            while (true) {
                val msgLen = try {
                    input.readInt() // Read 4-byte length prefix
                } catch (e: IOException) {
                    throw TransferFailedException()
                }
                if (msgLen <= 0 || msgLen > 100 * 1024 * 1024) { // 100 MB sanity check
                    throw TransferFailedException()
                }

                val msgBytes = ByteArray(msgLen)
                var readSoFar = 0
                while (readSoFar < msgLen) {
                    val read = input.read(msgBytes, readSoFar, msgLen - readSoFar)
                    if (read == -1) throw TransferFailedException()
                    readSoFar += read
                }

                // Now decode as before
                val encryptedMessage = msgBytes.decodeToString()
                val decryptedMessage = try {
                    AesCipher.decrypt(encryptedMessage)
                } catch (e: Exception) {
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
}