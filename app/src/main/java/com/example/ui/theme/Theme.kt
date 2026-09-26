package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SpendWiseColors(
    val background: Color,
    val surface: Color,
    val softSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val softBlue: Color,
    val lavender: Color,
    val softPeach: Color,
    val warmYellow: Color,
    val softMint: Color,
    val softCoral: Color,
    val glassWhite: Color,
    val glassBorder: Color,
    val isDark: Boolean
)

val LocalSpendWiseColors = staticCompositionLocalOf {
    SpendWiseColors(
        background = SpendWiseBackgroundLight,
        surface = SpendWiseSurfaceLight,
        softSurface = SpendWiseSoftSurfaceLight,
        textPrimary = SpendWiseTextPrimaryLight,
        textSecondary = SpendWiseTextSecondaryLight,
        textMuted = SpendWiseTextMutedLight,
        softBlue = SpendWiseSoftBlue,
        lavender = SpendWiseLavender,
        softPeach = SpendWiseSoftPeach,
        warmYellow = SpendWiseWarmYellow,
        softMint = SpendWiseSoftMint,
        softCoral = SpendWiseSoftCoral,
        glassWhite = SpendWiseGlassWhite,
        glassBorder = SpendWiseGlassBorderLight,
        isDark = false
    )
}

private val LightSpendWiseColors = SpendWiseColors(
    background = SpendWiseBackgroundLight,
    surface = SpendWiseSurfaceLight,
    softSurface = SpendWiseSoftSurfaceLight,
    textPrimary = SpendWiseTextPrimaryLight,
    textSecondary = SpendWiseTextSecondaryLight,
    textMuted = SpendWiseTextMutedLight,
    softBlue = SpendWiseSoftBlue,
    lavender = SpendWiseLavender,
    softPeach = SpendWiseSoftPeach,
    warmYellow = SpendWiseWarmYellow,
    softMint = SpendWiseSoftMint,
    softCoral = SpendWiseSoftCoral,
    glassWhite = SpendWiseGlassWhite,
    glassBorder = SpendWiseGlassBorderLight,
    isDark = false
)

private val DarkSpendWiseColors = SpendWiseColors(
    background = SpendWiseBackgroundDark,
    surface = SpendWiseSurfaceDark,
    softSurface = SpendWiseSoftSurfaceDark,
    textPrimary = SpendWiseTextPrimaryDark,
    textSecondary = SpendWiseTextSecondaryDark,
    textMuted = SpendWiseTextMutedDark,
    softBlue = SpendWiseSoftBlue,
    lavender = SpendWiseLavender,
    softPeach = SpendWiseSoftPeach,
    warmYellow = SpendWiseWarmYellow,
    softMint = SpendWiseSoftMint,
    softCoral = SpendWiseSoftCoral,
    glassWhite = Color(0x1AFFFFFF),
    glassBorder = Color(0x2AFFFFFF),
    isDark = true
)

private val MaterialLightScheme = lightColorScheme(
    primary = SpendWiseTextPrimaryLight,
    onPrimary = Color.White,
    secondary = SpendWiseLavender,
    onSecondary = Color.Black,
    background = SpendWiseBackgroundLight,
    onBackground = SpendWiseTextPrimaryLight,
    surface = SpendWiseSurfaceLight,
    onSurface = SpendWiseTextPrimaryLight
)

private val MaterialDarkScheme = darkColorScheme(
    primary = SpendWiseTextPrimaryDark,
    onPrimary = Color.Black,
    secondary = SpendWiseLavender,
    onSecondary = Color.White,
    background = SpendWiseBackgroundDark,
    onBackground = SpendWiseTextPrimaryDark,
    surface = SpendWiseSurfaceDark,
    onSurface = SpendWiseTextPrimaryDark
)

object SpendWiseTheme {
    val colors: SpendWiseColors
        @Composable
        get() = LocalSpendWiseColors.current
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val spendWiseColors = if (darkTheme) DarkSpendWiseColors else LightSpendWiseColors
    val materialScheme = if (darkTheme) MaterialDarkScheme else MaterialLightScheme

    CompositionLocalProvider(LocalSpendWiseColors provides spendWiseColors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = Typography,
            content = content
        )
    }
}
