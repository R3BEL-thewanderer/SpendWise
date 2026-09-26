package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.SpendWiseViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.formatCurrency
import com.example.ui.theme.SpendWiseTheme

@Composable
fun AnalyticsScreen(viewModel: SpendWiseViewModel) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary
    val isDark = SpendWiseTheme.colors.isDark
    val softBlueColor = SpendWiseTheme.colors.softBlue
    val lavenderColor = SpendWiseTheme.colors.lavender
    val softCoralColor = SpendWiseTheme.colors.softCoral

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // Header with month selector
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Analytics",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Understand your spending better",
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isDark) Color(0x22FFFFFF) else Color.White.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpendWiseTheme.colors.glassBorder),
                        modifier = Modifier.clickable {
                            viewModel.showToast("Filtered: March 2025")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "March 2025",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Total Spending Card with glowing visual
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analytics_total_spending_card"),
                    shape = RoundedCornerShape(28.dp),
                    elevation = 6.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = if (isDark) {
                                        listOf(Color(0xFF222630), Color(0xFF191C22))
                                    } else {
                                        listOf(
                                            SpendWiseTheme.colors.softBlue.copy(alpha = 0.25f),
                                            SpendWiseTheme.colors.lavender.copy(alpha = 0.2f),
                                            Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Spending",
                                    fontSize = 14.sp,
                                    color = textSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF34C759).copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                        contentDescription = null,
                                        tint = Color(0xFF34C759),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "12%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34C759)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "₹ 24,580",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "vs last month (₹ 27,900)",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Spending Trend Spline Curve Chart
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Spending Trend",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = "Monthly",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SpendWiseTheme.colors.lavender
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Peak Callout Pill
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF171717))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "₹ 5,420 (Peak)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom Spline Curve Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            val w = size.width
                            val h = size.height

                            val points = listOf(
                                Offset(w * 0.05f, h * 0.75f),
                                Offset(w * 0.22f, h * 0.65f),
                                Offset(w * 0.40f, h * 0.25f), // Peak
                                Offset(w * 0.58f, h * 0.70f),
                                Offset(w * 0.76f, h * 0.40f),
                                Offset(w * 0.95f, h * 0.55f)
                            )

                            // Smooth cubic path
                            val path = Path().apply {
                                moveTo(points[0].x, points[0].y)
                                for (i in 0 until points.size - 1) {
                                    val p0 = points[i]
                                    val p1 = points[i + 1]
                                    val controlX = (p0.x + p1.x) / 2
                                    cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                                }
                            }

                            // Gradient Fill under curve
                            val fillPath = Path().apply {
                                addPath(path)
                                lineTo(points.last().x, h)
                                lineTo(points.first().x, h)
                                close()
                            }

                            drawPath(
                                fillPath,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        softBlueColor.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )

                            // Line stroke
                            drawPath(
                                path,
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        softBlueColor,
                                        lavenderColor,
                                        softCoralColor
                                    )
                                ),
                                style = Stroke(width = 4f, cap = StrokeCap.Round)
                            )

                            // Dots on points
                            points.forEachIndexed { index, pt ->
                                val isPeak = index == 2
                                drawCircle(
                                    color = if (isPeak) lavenderColor else softBlueColor,
                                    radius = if (isPeak) 6f else 4.5f,
                                    center = pt
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = if (isPeak) 3f else 2f,
                                    center = pt
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Month Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { m ->
                                Text(
                                    text = m,
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Income vs Expenses Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Income
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34C759).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = Color(0xFF34C759),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(text = "Income", fontSize = 12.sp, color = textSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "₹ 45,000",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+ 8% vs last month",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF34C759)
                            )
                        }
                    }

                    // Expenses
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(SpendWiseTheme.colors.softCoral.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                        contentDescription = null,
                                        tint = SpendWiseTheme.colors.softCoral,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(text = "Expenses", fontSize = 12.sp, color = textSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "₹ 24,580",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "- 12% vs last month",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF34C759)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Category Breakdown Donut & Legend
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Category Breakdown",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = "See All",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SpendWiseTheme.colors.lavender
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Donut Chart Canvas
                            Box(
                                modifier = Modifier.size(140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(140.dp)) {
                                    val stroke = 18f
                                    val canvasSize = size.minDimension - stroke
                                    val topLeft = Offset(stroke / 2, stroke / 2)
                                    val arcSize = Size(canvasSize, canvasSize)

                                    val slices = listOf(
                                        32f to Color(0xFF9CC9FF),
                                        20f to Color(0xFFEF9C8D),
                                        18f to Color(0xFFF5D98A),
                                        13f to Color(0xFFC9B8FF),
                                        10f to Color(0xFFB9DEC9),
                                        7f to Color(0xFFF4C7B5)
                                    )

                                    var start = -90f
                                    slices.forEach { (pct, col) ->
                                        val sweep = (pct / 100f) * 360f
                                        drawArc(
                                            color = col,
                                            startAngle = start + 1.5f,
                                            sweepAngle = sweep - 3f,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                                        )
                                        start += sweep
                                    }
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "₹ 24,580",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "Total Spent",
                                        fontSize = 10.sp,
                                        color = textSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(18.dp))

                            // Legend
                            Column(modifier = Modifier.weight(1f)) {
                                val legendItems = listOf(
                                    Triple("Food & Drinks", "32%", Color(0xFF9CC9FF)),
                                    Triple("Shopping", "20%", Color(0xFFEF9C8D)),
                                    Triple("Bills & Utilities", "18%", Color(0xFFF5D98A)),
                                    Triple("Transport", "13%", Color(0xFFC9B8FF)),
                                    Triple("Entertainment", "10%", Color(0xFFB9DEC9)),
                                    Triple("Others", "7%", Color(0xFFF4C7B5))
                                )
                                legendItems.forEach { (name, pct, col) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(col))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = name, fontSize = 11.sp, color = textSecondary)
                                        }
                                        Text(text = pct, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Insight Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SpendWiseTheme.colors.warmYellow.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = SpendWiseTheme.colors.warmYellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Insight",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = "Your Food & Drinks spending is 18% lower than last month. Great job!",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }
}
