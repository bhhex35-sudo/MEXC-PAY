package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val MexcPayDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = DeepCharcoal,
    primaryContainer = NavySurfaceHigh,
    onPrimaryContainer = TextPrimary,
    secondary = CyanGlow,
    onSecondary = DeepCharcoal,
    secondaryContainer = NavySurfaceElevated,
    onSecondaryContainer = ElectricBlue,
    tertiary = EmeraldVerified,
    onTertiary = DeepCharcoal,
    tertiaryContainer = EmeraldSurface,
    onTertiaryContainer = EmeraldVerified,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = DeepCharcoal,
    surfaceContainerLow = NavyBackground,
    surfaceContainer = NavySurface,
    surfaceContainerHigh = NavySurfaceElevated,
    surfaceContainerHighest = NavySurfaceHigh,
    outline = NavyBorder,
    outlineVariant = NavySurfaceHigh,
    error = CoralAlert,
    onError = DeepCharcoal,
    errorContainer = CoralSurface,
    onErrorContainer = CoralAlert
)

val MexcPayShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = MexcPayDarkColorScheme,
        typography = Typography,
        shapes = MexcPayShapes,
        content = content
    )
}
