package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CineRed,
    secondary = CineGold,
    tertiary = CineAmber,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = OnDarkText,
    onSecondary = DarkBackground,
    onBackground = OnDarkText,
    onSurface = OnDarkText,
    onSurfaceVariant = OnDarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = CineAmber,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = OnLightText,
    onSecondary = LightSurface,
    onBackground = OnLightText,
    onSurface = OnLightText,
    onSurfaceVariant = OnLightTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We default to dark theme for cinema aesthetics
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
