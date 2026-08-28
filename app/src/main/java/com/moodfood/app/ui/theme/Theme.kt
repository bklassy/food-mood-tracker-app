package com.moodfood.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MoodFoodColorScheme = darkColorScheme(
    primary = CoralAccent,
    onPrimary = CreamText,
    secondary = SlotPill,
    onSecondary = CreamText,
    background = TealBackground,
    onBackground = CreamText,
    surface = BlushPink,
    onSurface = SlateText,
    surfaceVariant = SlotPill,
    onSurfaceVariant = SlateText,
)

/**
 * Mood & Food intentionally ignores system light/dark mode - always this
 * fixed dark-navy palette, by design, never a light theme.
 */
@Composable
fun MoodFoodTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MoodFoodColorScheme,
        typography = MoodFoodTypography,
        content = content,
    )
}
