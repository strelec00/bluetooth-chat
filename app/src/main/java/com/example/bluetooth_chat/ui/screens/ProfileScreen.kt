package com.example.bluetooth_chat.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bluetooth_chat.R
import com.example.bluetooth_chat.ui.components.NavbarBack
import com.example.bluetooth_chat.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profileImageUri = viewModel.profileImageUri.value
    val isSystemDark = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        viewModel.setInitialDarkMode(isSystemDark)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateProfileImage(uri)
    }

    Scaffold(
        topBar = {
            NavbarBack(title = "Profile", onBack = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = if (profileImageUri != null)
                    rememberAsyncImagePainter(profileImageUri)
                else
                    painterResource(id = R.drawable.profile4),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = viewModel.username.value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = viewModel.status.value,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileFieldRow(
                label = "Username",
                initialValue = viewModel.username.value
            ) { newValue ->
                viewModel.updateUsername(newValue)
            }

            ProfileFieldRow(
                label = "Status",
                initialValue = viewModel.status.value
            ) { newValue ->
                viewModel.updateStatus(newValue)
            }


        }
    }
}

@Composable
fun ProfileFieldRow(
    label: String,
    initialValue: String,
    onValueChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        var text by remember { mutableStateOf(initialValue) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit $label") },
            text = {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onValueChange(text)
                        showDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )
        Button(
            onClick = { showDialog = true },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = "Edit")
        }
    }
}