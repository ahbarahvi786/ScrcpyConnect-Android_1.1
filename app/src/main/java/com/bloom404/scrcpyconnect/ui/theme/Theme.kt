package com.bloom404.scrcpyconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    secondary = Indigo,
    background = DeepObsidian,
    surface = MidnightSlate,
    onPrimary = DeepObsidian,
    onSecondary = DeepObsidian,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = StatusError,
    onError = DeepObsidian,
    outline = CardBorder,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary
)

@Composable
fun ScrcpyConnectTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
