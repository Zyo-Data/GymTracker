package com.jorge.gymtracker.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = SteelersGold,
    onPrimary = SteelersBlack,
    primaryContainer = SteelersGold,
    onPrimaryContainer = SteelersBlack,
    secondary = Gray500,
    onSecondary = White,
    background = SteelersBlack,
    onBackground = White,
    surface = SurfaceBlack,
    onSurface = White,
    surfaceVariant = Gray700,
    onSurfaceVariant = White
)

@Composable
fun GymTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

