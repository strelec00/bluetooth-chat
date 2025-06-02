package com.example.bluetooth_chat.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
        background = DarkGrey,
        primary = DarkGrey,
        secondary = LightGrey,
        tertiary = DarkAshGrey,
        onPrimary = Color.White,
        onSecondary = EggshellGrey,
        onBackground = Color(0xFFA0EADE),
        onTertiary = Color(0xFF5C6784),
    )

    private val LightColorScheme = lightColorScheme(
        background = Color.White,
        primary = Color.White,
        secondary = MediumGrey,
        tertiary = Color.White,
        onPrimary = FieldMouseGrey,
        onSecondary = ElephantGrey ,
        onBackground = Color(0xFF4cb963),
        onTertiary = Color(0xFF157F1F),

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun BluetoothchatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}