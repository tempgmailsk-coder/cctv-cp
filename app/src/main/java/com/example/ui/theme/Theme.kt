package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SentinelCyan,
    onPrimary = Color(0xFF003544),
    primaryContainer = Color(0xFF004D63),
    onPrimaryContainer = Color(0xFFBCE9FF),
    secondary = SentinelBlue,
    onSecondary = Color(0xFF00344F),
    secondaryContainer = Color(0xFF004B70),
    onSecondaryContainer = Color(0xFFCAE6FF),
    tertiary = SecuritySafeGreen,
    onTertiary = Color(0xFF003920),
    error = SecurityAlertRed,
    onError = Color(0xFF680016),
    background = SecurityNavyDark,
    onBackground = SecurityTextPrimary,
    surface = SecurityNavyCard,
    onSurface = SecurityTextPrimary,
    surfaceVariant = SecurityNavyCardElevated,
    onSurfaceVariant = SecurityTextSecondary,
    outline = SecurityNavyBorder
)

private val LightColorScheme = darkColorScheme( // CCTV apps look best in dark surveillance mode
    primary = SentinelCyanVariant,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF001F2A),
    secondary = SentinelBlue,
    onSecondary = Color.White,
    tertiary = SecuritySafeGreen,
    error = SecurityAlertRed,
    background = SecurityNavyDark,
    onBackground = SecurityTextPrimary,
    surface = SecurityNavyCard,
    onSurface = SecurityTextPrimary,
    surfaceVariant = SecurityNavyCardElevated,
    onSurfaceVariant = SecurityTextSecondary,
    outline = SecurityNavyBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent surveillance branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
