package com.acacioswork.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = AccentGreen,
    tertiary = Purple80,
    background = BgDark,
    surface = BgCard,
    onPrimary = TextLight,
    onSecondary = BgDark,
    onTertiary = BgDark,
    onBackground = TextLight,
    onSurface = TextLight,
    error = AlertRed
)

@Composable
fun AcaciosWorkTheme(
    darkTheme: Boolean = true, // Forzamos por defecto tema oscuro para coincidir con la web
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
