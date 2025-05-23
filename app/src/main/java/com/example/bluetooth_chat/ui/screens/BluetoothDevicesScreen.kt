package com.example.bluetooth_chat.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.bluetooth_chat.R

@Composable

// List of devices in vicinity
fun BluetoothDevicesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Get Bluetooth adapter
    val bluetoothAdapter = remember {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    var searchQuery by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }

    // List of discovered devices
    val devices = remember { mutableStateListOf<BluetoothDevice>() }

    // Filtered list based on user search input
    val filteredDevices = remember(searchQuery, devices) {
        if (searchQuery.isBlank()) devices
        else devices.filter {
            val nameOrAddress = it.name ?: it.address ?: ""
            nameOrAddress.contains(searchQuery, ignoreCase = true)
        }
    }

    // Select icon based on dark/light theme
    val isDarkTheme = isSystemInDarkTheme()
    val bluetoothIcon =
        if (isDarkTheme) R.drawable.bluetooth_icon_white else R.drawable.bluetooth_icon_black

    // Customize text selection colors
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
    )

    // Check all needed Bluetooth permissions
    fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Permission launcher for requesting multiple permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Log.d("Bluetooth", "All permissions granted")
        } else {
            Log.d("Bluetooth", "Permissions denied: $permissions")
        }
    }

    // Request permissions on first composition
    LaunchedEffect(Unit) {
        if (!hasBluetoothPermissions()) {
            val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            permissionLauncher.launch(requiredPermissions)
        }
    }

    // Auto-stop scanning after 30 seconds
    LaunchedEffect(isScanning) {
        if (isScanning) {
            kotlinx.coroutines.delay(30000) // 30 seconds timeout
            isScanning = false
            bluetoothAdapter?.cancelDiscovery()
            Log.d("Bluetooth", "Scan timeout reached")
        }
    }

    // Broadcast receiver to catch found Bluetooth devices
    val receiver = rememberUpdatedState(
        object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }

                    // Add device if not already in list
                    if (device != null &&
                        ContextCompat.checkSelfPermission(
                            context,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                Manifest.permission.BLUETOOTH_CONNECT
                            else
                                Manifest.permission.BLUETOOTH
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (!devices.any { it.address == device.address }) {
                            devices.add(device)
                            Log.d("Bluetooth", "Device found: ${device.name ?: device.address}")
                        }
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == intent?.action) {
                    isScanning = false
                    Log.d("Bluetooth", "Discovery finished")
                }
            }
        }
    )

    // Start Bluetooth discovery
    fun startDiscovery() {
        if (hasBluetoothPermissions()) {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }

            context.registerReceiver(receiver.value, filter)

            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter.cancelDiscovery()
            }

            try {
                isScanning = bluetoothAdapter?.startDiscovery() == true
                if (isScanning) {
                    Log.d("Bluetooth", "Started discovery")
                } else {
                    Log.d("Bluetooth", "Failed to start discovery")
                }
            } catch (e: SecurityException) {
                isScanning = false
                e.printStackTrace()
            }
        } else {
            Log.d("Bluetooth", "Missing permissions to start discovery")
        }
    }

    // Cleanup receiver and cancel discovery when composable is disposed
    DisposableEffect(Unit) {
        startDiscovery()
        onDispose {
            try {
                context.unregisterReceiver(receiver.value)
                bluetoothAdapter?.cancelDiscovery()
                isScanning = false
            } catch (_: IllegalArgumentException) {
            }
        }
    }

    // Rotation animation for the scan button icon
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

    // UI Content
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search field with custom selection colors
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
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

        // Enhanced Scan button with animation
        Button(
            onClick = {
                // Cancel ongoing discovery when pressed again
                if (isScanning) {
                    bluetoothAdapter?.cancelDiscovery()
                    isScanning = false
                } else {
                    // Clear devices list when starting a new scan
                    devices.clear()
                    startDiscovery()
                }
            },
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
                // Animated icon
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Scan Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                        .then(if (isScanning) Modifier.rotate(rotation.value) else Modifier)
                )

                // Button text
                Text(
                    text = if (isScanning) "Scanning..." else "Scan for Devices",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Device count and section title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Available Devices",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${filteredDevices.size} found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // List of Bluetooth devices
        if (filteredDevices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "No Devices",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isScanning) "Searching for devices..." else "No devices found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDevices) { device ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable {
                                // Open Bluetooth settings when device item is clicked
                                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                context.startActivity(intent)
                            },
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            // Bluetooth icon
                            Image(
                                painter = painterResource(id = bluetoothIcon),
                                contentDescription = "Bluetooth Icon",
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(end = 12.dp)
                            )

                            // Device name or address
                            Text(
                                text = device.name ?: device.address ?: "Unknown Device",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}