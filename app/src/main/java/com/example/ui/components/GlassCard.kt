package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SpendWiseTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = SpendWiseTheme.colors.isDark
    val finalBg = backgroundColor ?: if (isDark) {
        Color(0xFF1E2127).copy(alpha = 0.75f)
    } else {
        Color.White.copy(alpha = 0.85f)
    }

    val finalBorder = borderColor ?: if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.White.copy(alpha = 0.7f)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(targetValue = if (isPressed && onClick != null) 0.98f else 1f, label = "cardScale")

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .scale(animatedScale)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0x1A000000),
                spotColor = if (isDark) Color(0x33C9B8FF) else Color(0x14000000)
            )
            .clip(shape)
            .then(clickModifier),
        shape = shape,
        color = finalBg,
        border = BorderStroke(borderWidth, finalBorder)
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    }
                )
            )
        ) {
            content()
        }
    }
}
