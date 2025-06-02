package com.example.bluetooth_chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.bluetooth_chat.data.chat.SimpleMessageStorage




@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    private val messageStorage: SimpleMessageStorage
) : ViewModel() {

    private var currentDeviceAddress: String? = null
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
        val name = messageStorage.getChatName(device.address)
        if (name == null) {
            // trigger UI to prompt for chat name
            _state.update { it.copy(promptForChatName = true) }
        }
        currentDeviceAddress = device.address
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }


    fun promptForChatName() {
        _state.update { it.copy(promptForChatName = true) }
    }

    fun saveChatName(name: String) {
        currentDeviceAddress?.let {
            messageStorage.saveChatName(it, name)
            _state.update { it.copy(promptForChatName = false) }
        }
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
                currentDeviceAddress?.let { address ->
                    messageStorage.saveMessage(bluetoothMessage, address)
                }
                _state.update {
                    it.copy(messages = it.messages + bluetoothMessage)
                }
            }
        }
    }

    fun sendFile(fileName: String, base64: String) {
        viewModelScope.launch {


                currentDeviceAddress?.let { address ->
                    val filePath = messageStorage.saveBase64File(base64, fileName, address)

                    val messageStr = "FILE:$fileName:$base64"

                    val bluetoothMessage = bluetoothController.trySendMessage(messageStr)

                    if (bluetoothMessage != null) {
                    val messageWithPath = bluetoothMessage.copy(filePath = filePath)
                    messageStorage.saveMessage(messageWithPath, address)
                    _state.update {
                        it.copy(messages = it.messages + messageWithPath)
                    }
                }
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

    fun getDisplayNameForDevice(device: BluetoothDeviceDomain): String {
        return messageStorage.getChatName(device.address)
            ?: device.name
            ?: "Unknown Device"
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when(result) {
                ConnectionResult.ConnectionEstablished -> {
                    val messages = currentDeviceAddress?.let { messageStorage.loadMessages(it) } ?: emptyList()
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            messages = messages
                        ) }
                }
                is ConnectionResult.TransferSucceeded -> {
                    currentDeviceAddress?.let { address ->
                        val msg = result.message
                        if (msg.message.startsWith("FILE:")) {
                            // Expected format: FILE:<fileName>:<base64>
                            val parts = msg.message.split(":", limit = 3)
                            if (parts.size == 3) {
                                val fileName = parts[1]
                                val base64 = parts[2]
                                // Save file bytes locally
                                val filePath = messageStorage.saveBase64File(base64, fileName, address)
                                // Update message with filePath
                                val updatedMessage = msg.copy(filePath = filePath)
                                messageStorage.saveMessage(updatedMessage, address)
                                _state.update { it.copy(messages = it.messages + updatedMessage) }
                            } else {
                                // Fallback: save message normally if parsing fails
                                messageStorage.saveMessage(msg, address)
                                _state.update { it.copy(messages = it.messages + msg) }
                            }
                        } else {
                            // Regular text message
                            messageStorage.saveMessage(msg, address)
                            _state.update { it.copy(messages = it.messages + msg) }
                        }
                    }
                }

                is ConnectionResult.Error -> {
                    _state.update { it.copy(
                        isConnected = false,
                        isConnecting = false,
                        errorMessage = result.message
                    ) }
                }
            }
        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _state.update { it.copy(
                    isConnected = false,
                    isConnecting = false,
                ) }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }


}
