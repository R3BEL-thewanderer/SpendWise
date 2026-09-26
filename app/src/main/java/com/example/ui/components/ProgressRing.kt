package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CategoryAllocation
import com.example.ui.theme.SpendWiseTheme

@Composable
fun MultiCategoryDonut(
    spent: Double,
    totalLimit: Double,
    allocations: List<CategoryAllocation>,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp,
    strokeWidth: Dp = 16.dp
) {
    val isDark = SpendWiseTheme.colors.isDark
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    val usedPercent = if (totalLimit > 0) ((spent / totalLimit) * 100).toInt() else 0
    val animatedProgress by animateFloatAsState(
        targetValue = if (totalLimit > 0) (spent / totalLimit).toFloat().coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "donutProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val canvasSize = this.size.minDimension - stroke
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(canvasSize, canvasSize)

            // Background Track
            drawArc(
                color = if (isDark) Color(0x1FFFFFFF) else Color(0x14000000),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Render category arcs proportionally
            var currentAngle = -90f
            val totalAllocation = allocations.sumOf { it.percentage }.coerceAtLeast(1)

            allocations.forEach { cat ->
                val segmentSweep = (cat.percentage.toFloat() / totalAllocation) * 360f * animatedProgress
                if (segmentSweep > 0.5f) {
                    val color = Color(cat.colorHex)
                    drawArc(
                        color = color,
                        startAngle = currentAngle + 2f,
                        sweepAngle = segmentSweep - 4f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                currentAngle += segmentSweep
            }
        }

        // Center Content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatCurrency(spent),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Text(
                text = "of ${formatCurrency(totalLimit)}",
                fontSize = 12.sp,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$usedPercent% used",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SpendWiseTheme.colors.lavender
            )
        }
    }
}

@Composable
fun SingleDonutProgress(
    percentage: Int,
    label: String = "used",
    color: Color = Color(0xFFEF9C8D),
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    strokeWidth: Dp = 14.dp
) {
    val isDark = SpendWiseTheme.colors.isDark
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    val animatedPercent by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "singleDonut"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val canvasSize = this.size.minDimension - stroke
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(canvasSize, canvasSize)

            // Track
            drawArc(
                color = if (isDark) Color(0x1FFFFFFF) else Color(0x14000000),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(color.copy(alpha = 0.6f), color, color)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedPercent,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = textSecondary
            )
        }
    }
}
