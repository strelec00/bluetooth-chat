package com.example.bluetooth_chat.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bluetooth_chat.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.navigation.NavHostController
import com.example.bluetooth_chat.domain.chat.BluetoothDevice
import com.example.bluetooth_chat.presentation.BluetoothViewModel

/**
 * ChatScreen lists users. When a user is selected it shows ChatInboxScreen inline,
 * and notifies parent via onDetailOpen whether detail view is active.
 */
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onDetailOpen: (Boolean) -> Unit,
    navController: NavHostController,
    onStartServer: () -> Unit,
    bluetoothViewModel: BluetoothViewModel
) {
    val scannedDevices = bluetoothViewModel.state.collectAsState().value.scannedDevices


    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<BluetoothDevice?>(null) }

    //filter logic
    val filteredDevices = remember(searchQuery,scannedDevices) {
        if (searchQuery.isBlank()) scannedDevices
        else scannedDevices.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }
    }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        if (selectedUser == null) {
            // notify parent that detail is closed
            LaunchedEffect(Unit) { onDetailOpen(false) }

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search people") },
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
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Button(
                    onClick = onStartServer,
                    modifier = Modifier
                        .height(44.dp)
                        .widthIn(min = 150.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                ) {
                    Text(
                        text = "Start server",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }




            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredDevices) { device ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable {
                                selectedUser = device
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
                            Image(
                                painter = painterResource(id = R.drawable.profile1),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Text(
                                text = device.name ?: "Unnamed device",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // notify parent detail is open
            LaunchedEffect(selectedUser) { onDetailOpen(true) }

            // Show inbox for selected user
            selectedUser?.let { user ->
                ChatInboxScreen(
                    state = bluetoothViewModel.state.collectAsState().value,
                    onDisconnect = { selectedUser = null },
                    onSendMessage = { message -> bluetoothViewModel.sendMessage(message) },
                    onSendFile = { fileName, base64 -> bluetoothViewModel.sendFile(fileName, base64) },
                    onBack = { selectedUser = null },
                    promptForChatName = { bluetoothViewModel.promptForChatName() }
                )
            }

        }
    }
}
