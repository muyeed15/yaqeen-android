package com.pyxlab.yaqeen.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrandColorScheme = lightColorScheme(
    primary = YaqeenGreen,
    onPrimary = Color.White,
    primaryContainer = YaqeenLime,
    onPrimaryContainer = YaqeenGreenDark,
    secondary = YaqeenGreenLight,
    onSecondary = Color.White,
    tertiary = YaqeenLimeDark,
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0F4F0),
    onSurfaceVariant = Color(0xFF44483F)
)

@Composable
fun YaqeenTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BrandColorScheme,
        typography = Typography,
        content = content
    )
}
