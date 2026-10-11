package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AstroBaseColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = SpaceDark,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = Color.White,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = SpaceCardBg,
    onSecondaryContainer = CyanAccent,
    tertiary = MarsRust,
    onTertiary = Color.White,
    background = SpaceDark,
    onBackground = TextPrimary,
    surface = SpaceAbyss,
    onSurface = TextPrimary,
    surfaceVariant = SpaceCardBg,
    onSurfaceVariant = TextSecondary,
    outline = SpaceCardBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun AstroBaseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AstroBaseColorScheme,
        typography = Typography,
        content = content
    )
}
