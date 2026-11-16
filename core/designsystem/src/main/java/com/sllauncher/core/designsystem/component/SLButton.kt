package com.sllauncher.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sllauncher.core.designsystem.theme.SLTheme

enum class SLButtonStyle { Primary, Secondary }

/**
 * Neo-brutalist button: flat fill, hard border and an offset block shadow.
 * Pressing it slides the face onto its shadow, which is the press feedback
 * (no ripple, no glow). Always at least 48dp tall for accessibility.
 */
@Composable
fun SLButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: SLButtonStyle = SLButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val colors = SLTheme.colors
    val shapes = SLTheme.shapes
    val spacing = SLTheme.spacing
    val shape = RoundedCornerShape(shapes.cornerSmall)

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val container = if (style == SLButtonStyle.Primary) colors.accent else colors.surface
    val content = if (style == SLButtonStyle.Primary) colors.onAccent else colors.textPrimary
    val border = if (style == SLButtonStyle.Primary) colors.accent else colors.border
    val shadow = shapes.blockShadowOffset
    val faceOffset = if (pressed && enabled) shadow else 0.dp

    Box(
        modifier = modifier
            .padding(end = shadow, bottom = shadow)
            .alpha(if (enabled) 1f else 0.4f),
        propagateMinConstraints = true,
    ) {
        // Block shadow sits behind the face, offset down-right.
        Box(
            Modifier
                .matchParentSize()
                .offset(shadow, shadow)
                .background(colors.border, shape),
        )
        Box(
            modifier = Modifier
                .offset(faceOffset, faceOffset)
                .defaultMinSize(minHeight = shapes.minTouchTarget)
                .background(container, shape)
                .border(shapes.borderWidth, border, shape)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(horizontal = spacing.xl, vertical = spacing.md),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text.uppercase(),
                style = SLTheme.typography.label,
                color = content,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(backgroundColor = 0xFF090909, showBackground = true)
@Composable
private fun SLButtonPreview() {
    SLTheme {
        Box(Modifier.padding(16.dp)) {
            SLButton(text = "Set as default", onClick = {})
        }
    }
}
