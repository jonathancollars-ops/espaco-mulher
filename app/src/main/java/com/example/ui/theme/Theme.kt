package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeColor {
    PURPLE, GREEN, BLUE
}

private fun getDarkColorScheme(themeColor: AppThemeColor) = darkColorScheme(
    primary = when (themeColor) {
        AppThemeColor.PURPLE -> PurplePrimaryDark
        AppThemeColor.GREEN -> GreenPrimaryDark
        AppThemeColor.BLUE -> BluePrimaryDark
    },
    secondary = when (themeColor) {
        AppThemeColor.PURPLE -> PurpleAccent
        AppThemeColor.GREEN -> GreenAccent
        AppThemeColor.BLUE -> BlueAccent
    },
    background = Color(0xFF1D1B20),
    surface = Color(0xFF1D1B20),
    surfaceVariant = Color(0xFF49454F),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFCAC4D0)
)

private fun getLightColorScheme(themeColor: AppThemeColor) = lightColorScheme(
    primary = when (themeColor) {
        AppThemeColor.PURPLE -> PurplePrimaryLight
        AppThemeColor.GREEN -> GreenPrimaryLight
        AppThemeColor.BLUE -> BluePrimaryLight
    },
    secondary = when (themeColor) {
        AppThemeColor.PURPLE -> PurpleAccent
        AppThemeColor.GREEN -> GreenAccent
        AppThemeColor.BLUE -> BlueAccent
    },
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1F1B16),
    onSurface = Color(0xFF1F1B16)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: AppThemeColor = AppThemeColor.PURPLE,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) getDarkColorScheme(themeColor) else getLightColorScheme(themeColor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
