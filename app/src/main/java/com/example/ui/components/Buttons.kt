package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SpendWiseTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showArrow: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = "primary_button",
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    height: Dp = 56.dp
) {
    val isDark = SpendWiseTheme.colors.isDark
    val finalBg = backgroundColor ?: if (isDark) Color(0xFFF5F5F2) else Color(0xFF171717)
    val finalContentColor = contentColor ?: if (isDark) Color(0xFF111214) else Color.White

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(targetValue = if (isPressed && enabled) 0.97f else 1f, label = "btnScale")

    Surface(
        modifier = modifier
            .testTag(testTag)
            .scale(animatedScale)
            .height(height)
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(height / 2))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(height / 2),
        color = if (enabled) finalBg else finalBg.copy(alpha = 0.5f),
        contentColor = finalContentColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    tint = finalContentColor
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = finalContentColor
            )
            if (showArrow && icon == null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(18.dp),
                    tint = finalContentColor
                )
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "secondary_button",
    height: Dp = 56.dp
) {
    val isDark = SpendWiseTheme.colors.isDark
    val bg = if (isDark) Color(0x1AFFFFFF) else Color.White.copy(alpha = 0.7f)
    val border = if (isDark) Color(0x2AFFFFFF) else Color(0x33000000)
    val textColor = SpendWiseTheme.colors.textPrimary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(targetValue = if (isPressed && enabled) 0.97f else 1f, label = "btnSecScale")

    Surface(
        modifier = modifier
            .testTag(testTag)
            .scale(animatedScale)
            .height(height)
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(height / 2))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(height / 2),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}
