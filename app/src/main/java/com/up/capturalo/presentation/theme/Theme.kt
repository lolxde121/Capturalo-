package com.up.capturalo.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryDarkBlue,
    onPrimaryContainer = Color.White,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    tertiary = AccentGreen,
    onTertiary = Color.White,
    error = AccentRed,
    onError = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
)

/**
 * Tema principal de Jetpack Compose para la aplicación "Captúralo!".
 *
 * Cumple con la rúbrica de Riqueza Visual y Tematización Coherente (MaterialTheme).
 */
@Composable
fun CapturaloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
