package com.example.aquacheck.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryDarkGreen,
    onPrimary = OnPrimaryWhite,
    primaryContainer = AptoContainerGreen,
    onPrimaryContainer = OnAptoContainerGreen,
    secondary = AptoContainerGreen,
    onSecondary = OnAptoContainerGreen,
    secondaryContainer = AptoContainerGreen,
    onSecondaryContainer = OnAptoContainerGreen,
    background = BackgroundLight,
    onBackground = OnSurfaceLightText,
    surface = SurfaceLight,
    onSurface = OnSurfaceLightText,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLightText,
    error = ObservadoRed,
    onError = OnPrimaryWhite,
    errorContainer = ObservadoContainerRed,
    onErrorContainer = ObservadoRed,
    outline = OutlineBorderLight
)

@Composable
fun AquaCheckTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
