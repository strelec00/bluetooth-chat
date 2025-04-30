// 1. First, create the information screen UI
package com.example.bluetooth_chat.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        InfoPage(
            title = "Welcome to Bluetooth Chat",
            description = "Connect and chat with nearby devices using Bluetooth technology",
            icon = Icons.Default.Warning
        ),
        InfoPage(
            title = "Search & Connect",
            description = "Discover nearby devices and connect with a simple tap",
            icon = Icons.Default.Warning
        ),
        InfoPage(
            title = "Secure Messaging",
            description = "Exchange messages securely with your connected devices",
            icon = Icons.Default.Warning
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Content takes most of the space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Display current info page with animation
            androidx.compose.animation.AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(animationSpec = tween(600)) { it / 2 }
            ) {
                InfoPageContent(page = pages[currentPage])
            }
        }

        // Bottom navigation and indicators
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            Row(
                modifier = Modifier
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (currentPage == index) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentPage == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Skip button - only on first pages
                if (currentPage < pages.size - 1) {
                    TextButton(onClick = onFinish) {
                        Text("Skip")
                    }
                } else {
                    // Empty spacer for alignment when skip button is not shown
                    Spacer(modifier = Modifier.width(64.dp))
                }

                // Next or Get Started button
                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onFinish()
                        }
                    }
                ) {
                    Text(
                        text = if (currentPage < pages.size - 1) "Next" else "Get Started",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoPageContent(page: InfoPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon in a colored circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberVectorPainter(image = page.icon),
                contentDescription = page.title,
                modifier = Modifier.size(64.dp),
                alpha = 0.9f
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

// Simple data class for info page content
data class InfoPage(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

