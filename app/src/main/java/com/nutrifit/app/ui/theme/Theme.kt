package com.nutrifit.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Cores da marca - verde saudável
val Green40 = Color(0xFF2E7D32)
val Green80 = Color(0xFF81C784)
val Green90 = Color(0xFFC8E6C9)
val Green10 = Color(0xFF1B5E20)

val Orange40 = Color(0xFFEF6C00)
val Orange80 = Color(0xFFFFB74D)

val Blue40 = Color(0xFF1565C0)
val Blue80 = Color(0xFF64B5F6)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green10,
    primaryContainer = Green40,
    secondary = Orange80,
    tertiary = Blue80
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    secondary = Orange40,
    tertiary = Blue40
)

@Composable
fun NutriFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
