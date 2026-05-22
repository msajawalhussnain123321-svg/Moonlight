package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Standard Moon Light brand scheme (representing premium, rich dark aesthetics)
private val DarkColorScheme = darkColorScheme(
    primary = LunarGold,
    onPrimary = NeutralDark,
    primaryContainer = LunarSurfaceLight,
    onPrimaryContainer = LunarGold,
    secondary = DeepSkyBlue,
    onSecondary = Color.Black,
    tertiary = CoralOrange,
    onTertiary = Color.White,
    background = MidnightBlue,
    onBackground = NeutralTextLight,
    surface = LunarSurface,
    onSurface = NeutralTextLight,
    surfaceVariant = LunarSurfaceLight,
    onSurfaceVariant = NeutralTextMuted,
    error = SlashedRed,
    onError = Color.White
)

// High-fidelity light representation of the brand
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFC7A400), // Richer gold for visibility on light surfaces
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF9C4),
    onPrimaryContainer = Color(0xFF5D4037),
    secondary = Color(0xFF0288D1),
    onSecondary = Color.White,
    tertiary = CoralOrange,
    onTertiary = Color.White,
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF1C1E24),
    surface = Color.White,
    onSurface = Color(0xFF1C1E24),
    surfaceVariant = Color(0xFFECEFF1),
    onSurfaceVariant = Color(0xFF546E7A),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic colors during branding so Moon Light's distinct theme shows on all systems
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
