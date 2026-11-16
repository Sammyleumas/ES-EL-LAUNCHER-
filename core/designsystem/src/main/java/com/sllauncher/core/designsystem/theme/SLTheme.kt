package com.sllauncher.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Root theme for every SL Launcher screen.
 *
 * SL tokens are exposed through [SLTheme.colors], [SLTheme.typography],
 * [SLTheme.spacing] and [SLTheme.shapes]. A Material 3 theme is also
 * provided, mapped onto the same tokens, so any Material component we use
 * (text fields, sliders, dialogs) inherits the SL identity instead of
 * Material's default purple.
 */
@Composable
fun SLTheme(
    colors: SLColors = DefaultSLColors,
    typography: SLTypography = DefaultSLTypography,
    content: @Composable () -> Unit,
) {
    val spacing = SLSpacing()
    val shapes = SLShapes()

    val materialColors = darkColorScheme(
        primary = colors.accent,
        onPrimary = colors.onAccent,
        secondary = colors.textPrimary,
        onSecondary = colors.background,
        background = colors.background,
        onBackground = colors.textPrimary,
        surface = colors.surface,
        onSurface = colors.textPrimary,
        surfaceVariant = colors.surfaceElevated,
        onSurfaceVariant = colors.textSecondary,
        outline = colors.border,
        error = colors.error,
    )
    val materialShapes = Shapes(
        extraSmall = RoundedCornerShape(shapes.cornerSmall),
        small = RoundedCornerShape(shapes.cornerSmall),
        medium = RoundedCornerShape(shapes.cornerMedium),
        large = RoundedCornerShape(shapes.cornerMedium),
        extraLarge = RoundedCornerShape(shapes.cornerMedium),
    )

    CompositionLocalProvider(
        LocalSLColors provides colors,
        LocalSLTypography provides typography,
        LocalSLSpacing provides spacing,
        LocalSLShapes provides shapes,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            shapes = materialShapes,
            content = content,
        )
    }
}

/** Accessors for the current SL design tokens. */
object SLTheme {
    val colors: SLColors
        @Composable @ReadOnlyComposable get() = LocalSLColors.current

    val typography: SLTypography
        @Composable @ReadOnlyComposable get() = LocalSLTypography.current

    val spacing: SLSpacing
        @Composable @ReadOnlyComposable get() = LocalSLSpacing.current

    val shapes: SLShapes
        @Composable @ReadOnlyComposable get() = LocalSLShapes.current
}
