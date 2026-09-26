package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SpendWiseTheme
import kotlin.math.sin

@Composable
fun DigitalGulak(
    progress: Float, // 0f to 1f
    currentAmount: Double,
    targetAmount: Double,
    modifier: Modifier = Modifier,
    size: Dp = 230.dp,
    showLabels: Boolean = true
) {
    val isDark = SpendWiseTheme.colors.isDark
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary
    val lavenderColor = SpendWiseTheme.colors.lavender
    val softBlueColor = SpendWiseTheme.colors.softBlue

    // Smooth animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "gulakFill"
    )

    // Gentle floating motion
    val infiniteTransition = rememberInfiniteTransition(label = "gulakMotion")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim"
    )

    // Gentle shimmer phase
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerPhase"
    )

    // Celebration pulse when 100%
    val celebrationScale by infiniteTransition.animateFloat(
        initialValue = if (animatedProgress >= 0.99f) 1f else 1f,
        targetValue = if (animatedProgress >= 0.99f) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebrateScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(celebrationScale)
            .offset(y = floatOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // 1. Ambient Glow behind Gulak
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        lavenderColor.copy(alpha = if (animatedProgress >= 0.99f) 0.5f else 0.25f),
                        softBlueColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.55f),
                    radius = w * 0.55f
                )
            )

            // 2. Piggy Bank Vessel Contour Path
            val bodyPath = Path().apply {
                val cx = w * 0.5f
                val cy = h * 0.55f
                val rx = w * 0.42f
                val ry = h * 0.36f
                // Main rounded body
                addRoundRect(
                    RoundRect(
                        left = cx - rx,
                        top = cy - ry,
                        right = cx + rx,
                        bottom = cy + ry,
                        radiusX = rx * 0.7f,
                        radiusY = ry * 0.7f
                    )
                )
            }

            // Ears
            val leftEar = Path().apply {
                moveTo(w * 0.26f, h * 0.26f)
                quadraticTo(w * 0.28f, h * 0.14f, w * 0.38f, h * 0.22f)
                close()
            }
            val rightEar = Path().apply {
                moveTo(w * 0.62f, h * 0.22f)
                quadraticTo(w * 0.72f, h * 0.14f, w * 0.74f, h * 0.26f)
                close()
            }

            // Feet (small rounded glass pods)
            val footRadius = w * 0.08f
            drawRoundRect(
                color = if (isDark) Color(0x33FFFFFF) else Color(0x66FFFFFF),
                topLeft = Offset(w * 0.25f, h * 0.84f),
                size = Size(footRadius * 1.5f, footRadius),
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawRoundRect(
                color = if (isDark) Color(0x33FFFFFF) else Color(0x66FFFFFF),
                topLeft = Offset(w * 0.62f, h * 0.84f),
                size = Size(footRadius * 1.5f, footRadius),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // Snout (front rounded feature)
            drawRoundRect(
                brush = Brush.radialGradient(
                    listOf(
                        Color.White.copy(alpha = 0.5f),
                        Color(0xFFEF9C8D).copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.16f, h * 0.54f),
                    radius = w * 0.12f
                ),
                topLeft = Offset(w * 0.08f, h * 0.46f),
                size = Size(w * 0.16f, h * 0.16f),
                cornerRadius = CornerRadius(24f, 24f)
            )
            // Snout nostrils
            drawCircle(Color(0xFFEF9C8D).copy(alpha = 0.6f), radius = 3.5f, center = Offset(w * 0.14f, h * 0.52f))
            drawCircle(Color(0xFFEF9C8D).copy(alpha = 0.6f), radius = 3.5f, center = Offset(w * 0.14f, h * 0.56f))

            // Draw Ears
            drawPath(leftEar, brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.4f), Color(0xFFC9B8FF).copy(alpha = 0.3f))))
            drawPath(rightEar, brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.4f), Color(0xFF9CC9FF).copy(alpha = 0.3f))))

            // 3. Translucent Crystal Glass Vessel Base
            drawPath(
                bodyPath,
                brush = Brush.radialGradient(
                    colors = if (isDark) {
                        listOf(Color(0x33FFFFFF), Color(0x11FFFFFF), Color(0x05FFFFFF))
                    } else {
                        listOf(Color(0x99FFFFFF), Color(0x55FFFFFF), Color(0x22FFFFFF))
                    },
                    center = Offset(w * 0.45f, h * 0.45f),
                    radius = w * 0.45f
                )
            )

            // 4. Liquid Fill inside Gulak (Clipped to body)
            clipPath(bodyPath) {
                val fillHeight = (h * 0.72f) * animatedProgress
                val liquidTop = (h * 0.91f) - fillHeight

                if (animatedProgress > 0.02f) {
                    val wavePath = Path().apply {
                        moveTo(0f, h)
                        lineTo(0f, liquidTop)
                        // Soft undulating wave crest
                        val waveStep = w / 4f
                        for (i in 0..4) {
                            val x = i * waveStep
                            val waveOffset = (sin((i * 1.5 + shimmerPhase * 0.05).toFloat()) * 5f)
                            lineTo(x, liquidTop + waveOffset)
                        }
                        lineTo(w, h)
                        close()
                    }

                    // Liquid Gradient (Lavender -> Soft Blue -> Mint -> Peach)
                    val liquidBrush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFC9B8FF).copy(alpha = 0.85f),
                            Color(0xFF9CC9FF).copy(alpha = 0.9f),
                            Color(0xFFB9DEC9).copy(alpha = 0.85f),
                            Color(0xFFF4C7B5).copy(alpha = 0.8f)
                        ),
                        startY = liquidTop,
                        endY = h
                    )
                    drawPath(wavePath, brush = liquidBrush)

                    // Liquid surface glowing meniscus line
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.9f),
                                Color(0xFFC9B8FF),
                                Color.Transparent
                            )
                        ),
                        start = Offset(w * 0.15f, liquidTop),
                        end = Offset(w * 0.85f, liquidTop),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )

                    // Small rising bubbles / sparkling light coins inside liquid
                    val b1Y = liquidTop + fillHeight * 0.4f + sin(shimmerPhase * 0.08f) * 8f
                    val b2Y = liquidTop + fillHeight * 0.7f + sin(shimmerPhase * 0.06f + 2f) * 10f
                    drawCircle(Color.White.copy(alpha = 0.6f), radius = 4f, center = Offset(w * 0.35f, b1Y))
                    drawCircle(Color.White.copy(alpha = 0.5f), radius = 5.5f, center = Offset(w * 0.65f, b2Y))
                }
            }

            // 5. Glass Vessel Outline & Crystal Highlights
            drawPath(
                bodyPath,
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White.copy(alpha = 0.9f),
                        Color(0xFF9CC9FF).copy(alpha = 0.4f),
                        Color(0xFFC9B8FF).copy(alpha = 0.7f),
                        Color(0xFFF4C7B5).copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.9f)
                    )
                ),
                style = Stroke(width = 3.5f)
            )

            // Curved Reflection Highlight (gives the 3D rounded crystal look)
            val highlightPath = Path().apply {
                moveTo(w * 0.3f, h * 0.3f)
                cubicTo(w * 0.45f, h * 0.25f, w * 0.65f, h * 0.28f, w * 0.75f, h * 0.36f)
            }
            drawPath(
                highlightPath,
                color = Color.White.copy(alpha = 0.6f),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            // 6. Coin Slot at the Top
            val slotW = w * 0.28f
            val slotH = 7f
            drawRoundRect(
                color = if (isDark) Color(0xFF22252A) else Color(0xFFD4CFC4),
                topLeft = Offset(w * 0.5f - slotW / 2, h * 0.20f),
                size = Size(slotW, slotH),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Floating Golden Coin entering the slot
            val coinY = h * 0.12f + sin(shimmerPhase * 0.05f) * 4f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFF3B0), Color(0xFFF5D98A), Color(0xFFE2B743))
                ),
                radius = 12f,
                center = Offset(w * 0.5f, coinY)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = 4f,
                center = Offset(w * 0.48f, coinY - 3f)
            )

            // 7. Ambient Sparkles around Gulak
            if (animatedProgress >= 0.99f) {
                // Celebration golden confetti / starbursts
                val stars = listOf(
                    Offset(w * 0.12f, h * 0.2f),
                    Offset(w * 0.88f, h * 0.25f),
                    Offset(w * 0.18f, h * 0.8f),
                    Offset(w * 0.85f, h * 0.75f),
                    Offset(w * 0.5f, h * 0.04f)
                )
                stars.forEachIndexed { i, pt ->
                    val starRadius = 5f + (i % 3) * 2f
                    drawCircle(Color(0xFFF5D98A), radius = starRadius, center = pt)
                    drawCircle(Color.White, radius = starRadius * 0.5f, center = pt)
                }
            }
        }

        // Overlay Text Inside the Gulak
        if (showLabels) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = 12.dp)
            ) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary
                )
                Text(
                    text = formatCurrency(currentAmount),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "of ${formatCurrency(targetAmount)}",
                    fontSize = 11.sp,
                    color = textSecondary
                )
                if (animatedProgress >= 0.99f) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Goal Reached 🎉",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34C759)
                    )
                }
            }
        }
    }
}
