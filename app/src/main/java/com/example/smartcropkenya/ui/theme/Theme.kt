package com.example.smartcropkenya.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = Green700,
    onPrimary          = White,
    primaryContainer   = Green50,
    onPrimaryContainer = Green700,
    secondary          = Amber700,
    onSecondary        = White,
    background         = Grey100,
    onBackground       = Grey900,
    surface            = White,
    onSurface          = Grey900,
    surfaceVariant     = Grey100,
    onSurfaceVariant   = Grey700,
    outline            = Grey400,
    error              = Color(0xFFB00020),
    onError            = White
)

@Composable
fun SmartCropKenyaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}