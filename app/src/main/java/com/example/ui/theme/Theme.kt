package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dynamic custom theme token holder
data class CozyThemeColors(
    val isNight: Boolean,
    val canvasBg: Color,
    val cardSurface: Color,
    val cardElevated: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val accentTertiary: Color,
    val dockBg: Color,
    val meshMint: Color,
    val meshLavender: Color,
    val meshPeach: Color
)

val LocalCozyTheme = compositionLocalOf {
    CozyThemeColors(
        isNight = true,
        canvasBg = VelvetMidnightBg,
        cardSurface = ObsidianGlassCard,
        cardElevated = ObsidianGlassCardElevated,
        cardBorder = MidnightBorder,
        textPrimary = MidnightTextPrimary,
        textSecondary = MidnightTextSecondary,
        textMuted = MidnightTextMuted,
        accentPrimary = BioluminescentAqua,
        accentSecondary = BioluminescentViolet,
        accentTertiary = BioluminescentAmber,
        dockBg = ObsidianGlassCard,
        meshMint = BioluminescentAqua.copy(alpha = 0.12f),
        meshLavender = BioluminescentViolet.copy(alpha = 0.12f),
        meshPeach = BioluminescentAmber.copy(alpha = 0.08f)
    )
}

val CozyDaylightColors = CozyThemeColors(
    isNight = false,
    canvasBg = OatmilkBg,
    cardSurface = OatmilkSurface,
    cardElevated = OatmilkSurfaceCard,
    cardBorder = OatmilkBorder,
    textPrimary = DeepEspressoText,
    textSecondary = WarmStoneText,
    textMuted = WarmMutedText,
    accentPrimary = ReactorPrimary,
    accentSecondary = HelixPrimary,
    accentTertiary = AtlasPrimary,
    dockBg = OatmilkSurface.copy(alpha = 0.92f),
    meshMint = PastelMint.copy(alpha = 0.5f),
    meshLavender = PastelLavender.copy(alpha = 0.45f),
    meshPeach = PastelPeach.copy(alpha = 0.45f)
)

val BioluminescentNightColors = CozyThemeColors(
    isNight = true,
    canvasBg = VelvetMidnightBg,
    cardSurface = ObsidianGlassCard,
    cardElevated = ObsidianGlassCardElevated,
    cardBorder = MidnightBorder,
    textPrimary = MidnightTextPrimary,
    textSecondary = MidnightTextSecondary,
    textMuted = MidnightTextMuted,
    accentPrimary = BioluminescentAqua,
    accentSecondary = BioluminescentViolet,
    accentTertiary = BioluminescentAmber,
    dockBg = ObsidianGlassCard.copy(alpha = 0.92f),
    meshMint = BioluminescentAqua.copy(alpha = 0.14f),
    meshLavender = BioluminescentViolet.copy(alpha = 0.14f),
    meshPeach = BioluminescentAmber.copy(alpha = 0.10f)
)

private val M3DarkColorScheme = darkColorScheme(
    primary = BioluminescentAqua,
    onPrimary = VelvetMidnightBg,
    primaryContainer = ObsidianGlassCard,
    onPrimaryContainer = BioluminescentAqua,
    secondary = BioluminescentViolet,
    onSecondary = VelvetMidnightBg,
    tertiary = BioluminescentAmber,
    background = VelvetMidnightBg,
    surface = ObsidianGlassCard,
    onBackground = MidnightTextPrimary,
    onSurface = MidnightTextPrimary,
    outline = MidnightBorder
)

private val M3LightColorScheme = lightColorScheme(
    primary = ReactorPrimary,
    onPrimary = Color.White,
    primaryContainer = OatmilkSurfaceCard,
    onPrimaryContainer = ReactorDark,
    secondary = HelixPrimary,
    onSecondary = Color.White,
    tertiary = AtlasPrimary,
    background = OatmilkBg,
    surface = OatmilkSurface,
    onBackground = DeepEspressoText,
    onSurface = DeepEspressoText,
    outline = OatmilkBorder
)

@Composable
fun HariomBotTheme(
    isNightMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val cozyColors = if (isNightMode) BioluminescentNightColors else CozyDaylightColors
    val m3Colors = if (isNightMode) M3DarkColorScheme else M3LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isNightMode
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isNightMode
            }
        }
    }

    CompositionLocalProvider(LocalCozyTheme provides cozyColors) {
        MaterialTheme(
            colorScheme = m3Colors,
            typography = Typography,
            content = content
        )
    }
}
