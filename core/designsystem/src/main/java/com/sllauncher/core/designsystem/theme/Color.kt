package com.sllauncher.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Raw brand palette. Never reference these from feature code directly;
 * use [SLColors] through `SLTheme.colors` so themes can be swapped later.
 */
internal object SLPalette {
    val Black = Color(0xFF090909)
    val Charcoal = Color(0xFF151515)
    val Graphite = Color(0xFF252525)
    val Yellow = Color(0xFFFFD400)
    val OffWhite = Color(0xFFF5F1E8)
    val Grey = Color(0xFFA5A5A5)
    val BorderGrey = Color(0xFF383838)
    val ErrorRed = Color(0xFFFF5A4E)
}

/**
 * Semantic colour roles for SL Launcher. Names describe purpose, not hue,
 * so a future user theme can change the values without touching screens.
 */
@Immutable
data class SLColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val accent: Color,
    val onAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val error: Color,
)

val DefaultSLColors = SLColors(
    background = SLPalette.Black,
    surface = SLPalette.Charcoal,
    surfaceElevated = SLPalette.Graphite,
    accent = SLPalette.Yellow,
    onAccent = SLPalette.Black,
    textPrimary = SLPalette.OffWhite,
    textSecondary = SLPalette.Grey,
    border = SLPalette.BorderGrey,
    error = SLPalette.ErrorRed,
)

internal val LocalSLColors = staticCompositionLocalOf { DefaultSLColors }
