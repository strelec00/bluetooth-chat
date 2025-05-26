package com.example.bluetooth_chat.ui.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth_chat.domain.chat.BluetoothDevice
import com.example.bluetooth_chat.presentation.BluetoothUiState

@Composable
fun BluetoothDevicesScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onStartServer: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Automatically stop scan after 30 seconds
    LaunchedEffect(state.isScanning) {
        if (state.isScanning) {
            kotlinx.coroutines.delay(30_000)
            onStopScan()
            Log.d("Bluetooth", "Scan stopped after 30 seconds")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )

        ScanButton(
            isScanning = state.isScanning,
            onClick = {
                if (!state.isScanning) onStartScan()
            }
        )

        Button(onClick = onStartServer){
            Text(text = "Start server")
        }

        BluetoothDeviceList(
            pairedDevices = state.pairedDevices,
            scannedDevices = state.scannedDevices,
            onClick = onDeviceClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Filter Bluetooth devices") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Composable
fun ScanButton(
    isScanning: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanAnimation")
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAnimation"
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isScanning)
                MaterialTheme.colorScheme.tertiary
            else
                MaterialTheme.colorScheme.secondary,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .height(50.dp)
            .animateContentSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Scan Icon",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
                    .then(if (isScanning) Modifier.rotate(rotation.value) else Modifier)
            )
            Text(
                text = if (isScanning) "Scanning..." else "Scan for Devices",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        if (pairedDevices.isNotEmpty()) {
            item {
                Text(
                    text = "Paired Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            items(pairedDevices) { device ->
                Text(
                    text = device.name ?: "(No name)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(device) }
                        .padding(16.dp)
                )
            }
        }

        if (scannedDevices.isNotEmpty()) {
            item {
                Text(
                    text = "Scanned Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            items(scannedDevices) { device ->
                Text(
                    text = device.name ?: "(No name)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(device) }
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun DeviceItem(device: BluetoothDevice, onClick: (BluetoothDevice) -> Unit) {
    Text(
        text = device.name ?: "(No name)",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(device) }
            .padding(16.dp)
    )
}
