package com.example.bluetooth_chat.presentation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.util.Base64
import java.io.File
import java.io.FileOutputStream
import com.example.bluetooth_chat.domain.chat.BluetoothController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.bluetooth_chat.domain.chat.BluetoothDeviceDomain
import com.example.bluetooth_chat.domain.chat.ConnectionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import com.example.bluetooth_chat.domain.chat.BluetoothMessage
import android.app.Application
import com.example.bluetooth_chat.data.chat.MessageStorage


@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    application: Application
) : AndroidViewModel(application) {


    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messages = if(state.isConnected) state.messages else emptyList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    init {
        val savedMessages = MessageStorage.loadMessages(getApplication())
        _state.update { it.copy(messages = savedMessages) }
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _state.update { it.copy(
                errorMessage = error
            ) }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update { it.copy(
            isConnecting = false,
            isConnected = false
        ) }
    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.trySendMessage(message)
            if(bluetoothMessage != null) {


                _state.update { it.copy(
                    messages = it.messages + bluetoothMessage
                ) }
                MessageStorage.saveMessages(getApplication(), _state.value.messages)
            }
        }
    }

    fun sendFile(device: BluetoothDeviceDomain,fileName: String, base64: String) {
        viewModelScope.launch {
            // Compose the header as handled in the MessageMapper
            val bluetoothMessage = BluetoothMessage(
                message = base64,
                senderName = bluetoothController.getLocalDeviceName(), // or similar
                isFromLocalUser = true,
                isFile = true,
                fileName = "history_${device.address}.json",
                fileSize = base64.length.toLong() // or actual file size in bytes if you have it
            )
            val sentMessage = bluetoothController.trySendBluetoothMessage(bluetoothMessage)
            if (sentMessage != null) {
                _state.update { it.copy(messages = it.messages + sentMessage) }
            }
        }
    }

    fun startScan() {
        _state.value = _state.value.copy(isScanning = true)
        bluetoothController.startDiscovery()

        // Auto-stop scan after 30 seconds
        viewModelScope.launch {
            kotlinx.coroutines.delay(30_000)
            stopScan()
        }
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
        _state.value = _state.value.copy(isScanning = false)
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when(result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update { it.copy(
                        isConnected = true,
                        isConnecting = false,
                        errorMessage = null
                    ) }
                }
                is ConnectionResult.TransferSucceeded -> {
                    val msg = result.message
                    val updatedMsg = if (msg.isFile) {
                        // Save the file and set localFilePath
                        saveReceivedFile(getApplication<Application>().applicationContext, msg)
                    } else msg
                    _state.update { it.copy(
                        messages = it.messages + updatedMsg
                    ) }
                }
                is ConnectionResult.Error -> {
                    _state.update { it.copy(
                        isConnected = false,
                        isConnecting = false,
                        errorMessage = result.message
                    ) }
                }
            }
        }.catch { throwable ->
            bluetoothController.closeConnection()
            _state.update { it.copy(
                isConnected = false,
                isConnecting = false,
            ) }
        }.launchIn(viewModelScope)
    }


    /**
     * Save received file to storage and return the message with the local file path.
     */
    private fun saveReceivedFile(context: Context, message: BluetoothMessage): BluetoothMessage {
        if (!message.isFile || message.fileName == null) return message
        return try {
            val fileBytes = Base64.decode(message.message, Base64.DEFAULT)
            val dir = File(context.getExternalFilesDir(null), "ReceivedFiles")
            dir.mkdirs()
            val file = File(dir, message.fileName)
            FileOutputStream(file).use { it.write(fileBytes) }
            message.copy(localFilePath = file.absolutePath)
        } catch (e: Exception) {
            message // If saving fails, return the message as-is
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}
