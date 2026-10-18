package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GirlyLightColorScheme = lightColorScheme(
    primary = GirlyPinkPrimary,
    onPrimary = Color.White,
    primaryContainer = GirlyPinkContainer,
    onPrimaryContainer = GirlyPinkDeep,

    secondary = GirlyPinkLight,
    onSecondary = Color.White,
    secondaryContainer = GirlyPinkHighlight,
    onSecondaryContainer = GirlyPinkDeep,

    tertiary = GirlyPeachAccent,
    onTertiary = Color.White,
    tertiaryContainer = GirlyAmberContainer,
    onTertiaryContainer = GirlyTextPrimary,

    background = GirlyCanvasBackground,
    onBackground = GirlyTextPrimary,

    surface = GirlyCardSurface,
    onSurface = GirlyTextPrimary,
    surfaceVariant = GirlyCardSubtle,
    onSurfaceVariant = GirlyTextSecondary,

    outline = GirlyCardBorder,
    outlineVariant = GirlyDividerColor,
    error = Color(0xFFD3455B),
    onError = Color.White
)

private val GirlyDarkColorScheme = darkColorScheme(
    primary = GirlyPinkLight,
    onPrimary = GirlyPinkDeep,
    primaryContainer = Color(0xFF521E2C),
    onPrimaryContainer = GirlyPinkSoft,

    secondary = GirlyPinkSoft,
    onSecondary = Color(0xFF421522),
    secondaryContainer = Color(0xFF45222D),
    onSecondaryContainer = GirlyPinkHighlight,

    tertiary = GirlyPeachAccent,
    onTertiary = Color(0xFF452018),
    tertiaryContainer = Color(0xFF4A2B20),
    onTertiaryContainer = GirlyAmberContainer,

    background = Color(0xFF241419),
    onBackground = Color(0xFFFDEEF2),

    surface = Color(0xFF2E1A21),
    onSurface = Color(0xFFFDEEF2),
    surfaceVariant = Color(0xFF382029),
    onSurfaceVariant = Color(0xFFE2B4C1),

    outline = Color(0xFF5C3340),
    outlineVariant = Color(0xFF472832),
    error = Color(0xFFE26D8B),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GirlyDarkColorScheme else GirlyLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
