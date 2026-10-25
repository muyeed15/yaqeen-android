package com.pyxlab.yaqeen.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
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

private val DarkColorScheme = darkColorScheme(
    primary = YaqeenGreenLight,
    onPrimary = YaqeenGreenDark,
    primaryContainer = YaqeenGreen,
    onPrimaryContainer = YaqeenLime,
    secondary = YaqeenLime,
    onSecondary = Color(0xFF1C1B1F),
    tertiary = YaqeenLime,
    onTertiary = Color(0xFF1C1B1F),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant
)

@Composable
fun YaqeenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
