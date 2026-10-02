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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
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
import com.example.domain.ai.model.InsightSeverity
import com.example.domain.ai.model.InsightType
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

    val analytics = viewModel.getAnalytics(com.example.domain.model.DatePeriod.CURRENT_MONTH)

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
                            viewModel.showToast("Filtered: Current Period")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current Month",
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
                                        text = "${analytics.totals.savingsRate.toInt()}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34C759)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = formatCurrency(analytics.totals.totalExpenses),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Savings: ${formatCurrency(analytics.totals.savings)}",
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
                val peakTrend = analytics.monthlyTrends.maxByOrNull { it.expenses }
                val peakAmount = peakTrend?.expenses ?: 0.0

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
                                text = "${formatCurrency(peakAmount)} (Peak)",
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
                            val trends = analytics.monthlyTrends

                            val points = if (trends.isNotEmpty() && peakAmount > 0.0) {
                                trends.mapIndexed { index, item ->
                                    val x = w * (0.05f + (index.toFloat() / (trends.size - 1).coerceAtLeast(1)) * 0.90f)
                                    val normalizedY = (item.expenses / peakAmount).toFloat().coerceIn(0f, 1f)
                                    val y = h * (0.85f - normalizedY * 0.60f)
                                    Offset(x, y)
                                }
                            } else {
                                listOf(
                                    Offset(w * 0.05f, h * 0.75f),
                                    Offset(w * 0.22f, h * 0.65f),
                                    Offset(w * 0.40f, h * 0.25f),
                                    Offset(w * 0.58f, h * 0.70f),
                                    Offset(w * 0.76f, h * 0.40f),
                                    Offset(w * 0.95f, h * 0.55f)
                                )
                            }

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
                            val peakIndex = trends.indexOf(peakTrend)
                            points.forEachIndexed { index, pt ->
                                val isPeak = index == peakIndex
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
                            analytics.monthlyTrends.forEach { m ->
                                Text(
                                    text = m.monthLabel,
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
                                text = formatCurrency(analytics.totals.totalIncome),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Net: ${formatCurrency(analytics.totals.netCashFlow)}",
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
                                text = formatCurrency(analytics.totals.totalExpenses),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Savings Rate: ${analytics.totals.savingsRate.toInt()}%",
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
                val activeBreakdown = analytics.categoryBreakdown.filter { it.spentAmount > 0.0 }
                val palette = listOf(
                    Color(0xFF9CC9FF),
                    Color(0xFFEF9C8D),
                    Color(0xFFF5D98A),
                    Color(0xFFC9B8FF),
                    Color(0xFFB9DEC9),
                    Color(0xFFF4C7B5)
                )

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

                                    if (activeBreakdown.isNotEmpty()) {
                                        var start = -90f
                                        activeBreakdown.take(6).forEachIndexed { idx, item ->
                                            val sweep = ((item.percentageOfTotal.toFloat() / 100f) * 360f)
                                            val col = palette[idx % palette.size]
                                            drawArc(
                                                color = col,
                                                startAngle = start + 1.5f,
                                                sweepAngle = (sweep - 3f).coerceAtLeast(1f),
                                                useCenter = false,
                                                topLeft = topLeft,
                                                size = arcSize,
                                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                                            )
                                            start += sweep
                                        }
                                    } else {
                                        drawArc(
                                            color = Color.LightGray.copy(alpha = 0.3f),
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formatCurrency(analytics.totals.totalExpenses),
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
                                if (activeBreakdown.isNotEmpty()) {
                                    activeBreakdown.take(6).forEachIndexed { idx, item ->
                                        val col = palette[idx % palette.size]
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
                                                Text(text = item.categoryName, fontSize = 11.sp, color = textSecondary)
                                            }
                                            Text(
                                                text = "${item.percentageOfTotal.toInt()}%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "No category expenses recorded",
                                        fontSize = 12.sp,
                                        color = textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Monthly Financial Summary Card (AI Grounded)
            item {
                val monthlySummary = viewModel.getMonthlyAiSummary("Current Month")

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analytics_monthly_ai_summary_card"),
                    shape = RoundedCornerShape(26.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(SpendWiseTheme.colors.lavender, SpendWiseTheme.colors.softBlue)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI",
                                        tint = Color(0xFF171717),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Monthly Financial Summary",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "Deterministic metrics + AI intelligence",
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SpendWiseTheme.colors.lavender.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "Phase 4 AI",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) SpendWiseTheme.colors.lavender else Color(0xFF6B4EE6),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Structured Metrics Grid
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isDark) Color(0x22FFFFFF) else Color(0xFFF7F6F2))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Income", fontSize = 13.sp, color = textSecondary)
                                Text(
                                    text = formatCurrency(monthlySummary.income),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Expenses", fontSize = 13.sp, color = textSecondary)
                                Text(
                                    text = formatCurrency(monthlySummary.expenses),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Savings", fontSize = 13.sp, color = textSecondary)
                                Text(
                                    text = "${formatCurrency(monthlySummary.savings)} (${monthlySummary.savingsRate.toInt()}%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34C759)
                                )
                            }
                            if (monthlySummary.topCategory != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Top category", fontSize = 13.sp, color = textSecondary)
                                    Text(
                                        text = "${monthlySummary.topCategory} — ${formatCurrency(monthlySummary.topCategoryAmount)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Budget usage", fontSize = 13.sp, color = textSecondary)
                                Text(
                                    text = "${monthlySummary.budgetUsagePct.toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (monthlySummary.budgetUsagePct > 90) SpendWiseTheme.colors.softCoral else textPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // AI Explanation Box
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDark) Color(0x332A2438) else SpendWiseTheme.colors.lavender.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SpendWiseTheme.colors.lavender.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = if (isDark) SpendWiseTheme.colors.lavender else Color(0xFF6B4EE6),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Pattern Explanation",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) SpendWiseTheme.colors.lavender else Color(0xFF6B4EE6)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = monthlySummary.aiExplanation,
                                    fontSize = 12.sp,
                                    color = textPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Button to ask AI assistant
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SpendWiseTheme.colors.lavender,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openAiAssistant() }
                                .testTag("analytics_ask_ai_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF171717),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ask SpendWise AI for Details",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF171717)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Smart Actionable Insights List
            item {
                val insights = viewModel.getSmartInsights()
                if (insights.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Smart Financial Insights",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )

                        insights.take(4).forEach { insight ->
                            val accentColor = when (insight.severity) {
                                InsightSeverity.WARNING -> SpendWiseTheme.colors.softCoral
                                InsightSeverity.SUCCESS -> Color(0xFF34C759)
                                InsightSeverity.INFO -> SpendWiseTheme.colors.softBlue
                            }

                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openAiAssistant() }
                                    .testTag("analytics_insight_${insight.id}"),
                                shape = RoundedCornerShape(18.dp),
                                elevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(accentColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val icon = when (insight.severity) {
                                            InsightSeverity.WARNING -> Icons.Default.Warning
                                            InsightSeverity.SUCCESS -> Icons.Default.CheckCircle
                                            InsightSeverity.INFO -> Icons.Default.Lightbulb
                                        }
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = insight.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = insight.description,
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }
}
