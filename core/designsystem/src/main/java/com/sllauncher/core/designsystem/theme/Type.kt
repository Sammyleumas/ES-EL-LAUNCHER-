package com.sllauncher.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * SL type scale. Phase 1 uses the platform sans-serif; Phase 2 replaces
 * [SLFontFamily] with a bundled typeface so every device renders identically.
 * All sizes are in `sp`, so they follow the user's system font-size setting.
 */
internal val SLFontFamily: FontFamily = FontFamily.SansSerif

@Immutable
data class SLTypography(
    val display: TextStyle,
    val headline: TextStyle,
    val title: TextStyle,
    val body: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
)

val DefaultSLTypography = SLTypography(
    display = TextStyle(
        fontFamily = SLFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 64.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.02).em,
    ),
    headline = TextStyle(
        fontFamily = SLFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.01).em,
    ),
    title = TextStyle(
        fontFamily = SLFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    body = TextStyle(
        fontFamily = SLFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    label = TextStyle(
        fontFamily = SLFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.06.em,
    ),
    caption = TextStyle(
        fontFamily = SLFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

internal val LocalSLTypography = staticCompositionLocalOf { DefaultSLTypography }
