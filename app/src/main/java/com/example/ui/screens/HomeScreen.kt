package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ai.model.InsightSeverity
import com.example.domain.ai.model.InsightType
import com.example.domain.ai.model.SmartInsight
import com.example.state.ActiveModal
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassCard
import com.example.ui.components.TransactionRow
import com.example.ui.components.formatCurrency
import com.example.ui.theme.SpendWiseTheme

@Composable
fun HomeScreen(
    viewModel: SpendWiseViewModel,
    modifier: Modifier = Modifier
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary
    val isDark = SpendWiseTheme.colors.isDark

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        // Atmospheric ambient pastel glow at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SpendWiseTheme.colors.lavender.copy(alpha = if (isDark) 0.12f else 0.22f),
                            SpendWiseTheme.colors.softBlue.copy(alpha = if (isDark) 0.08f else 0.15f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // Header: Greeting, Avatar, Notification
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good Morning,",
                            fontSize = 14.sp,
                            color = textSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = viewModel.userProfile.name.split(" ").firstOrNull() ?: "Ashish",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "☀️", fontSize = 20.sp)
                        }
                        Text(
                            text = "Let's make today a smart one.",
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.showToast("No new notifications")
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x1AFFFFFF) else Color.White.copy(alpha = 0.8f))
                                .testTag("home_notification_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(SpendWiseTheme.colors.softPeach, SpendWiseTheme.colors.lavender)
                                    )
                                )
                                .clickable { viewModel.navigateTo(AppScreen.PROFILE) }
                                .testTag("home_avatar_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.userProfile.name.take(1).uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF171717)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Total Balance Card
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_balance_card"),
                    shape = RoundedCornerShape(28.dp),
                    elevation = 6.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = if (isDark) {
                                        listOf(
                                            Color(0xFF22262F).copy(alpha = 0.8f),
                                            Color(0xFF1B1D22).copy(alpha = 0.9f)
                                        )
                                    } else {
                                        listOf(
                                            SpendWiseTheme.colors.softBlue.copy(alpha = 0.25f),
                                            SpendWiseTheme.colors.lavender.copy(alpha = 0.2f),
                                            SpendWiseTheme.colors.softPeach.copy(alpha = 0.2f)
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Total Balance",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textSecondary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = if (viewModel.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Balance",
                                        tint = textSecondary,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { viewModel.isBalanceVisible = !viewModel.isBalanceVisible }
                                    )
                                }

                                // Arrow to Analytics
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0x22FFFFFF) else Color.White.copy(alpha = 0.8f))
                                        .clickable { viewModel.navigateTo(AppScreen.ANALYTICS) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Analytics",
                                        tint = textPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (viewModel.isBalanceVisible) formatCurrency(viewModel.totalBalance) else "••••••••",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF34C759).copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF34C759),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "8% vs last month",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF34C759)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Quick Actions: 4 rounded square glass cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Add,
                        label = "Add\nExpense",
                        iconColor = SpendWiseTheme.colors.softBlue,
                        onClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) },
                        testTag = "quick_add_expense"
                    )
                    QuickActionButton(
                        icon = Icons.Default.ArrowUpward,
                        label = "Add\nIncome",
                        iconColor = Color(0xFF34C759),
                        onClick = { viewModel.navigateTo(AppScreen.ADD_INCOME) },
                        testTag = "quick_add_income"
                    )
                    QuickActionButton(
                        icon = Icons.Default.QrCodeScanner,
                        label = "Scan\nReceipt",
                        iconColor = SpendWiseTheme.colors.lavender,
                        onClick = { viewModel.activeModal = ActiveModal.SCAN_RECEIPT },
                        testTag = "quick_scan_receipt"
                    )
                    QuickActionButton(
                        icon = Icons.Default.MoreHoriz,
                        label = "More\nCategories",
                        iconColor = SpendWiseTheme.colors.softPeach,
                        onClick = { viewModel.navigateTo(AppScreen.CATEGORIES) },
                        testTag = "quick_more_actions"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // SpendWise AI Assistant Banner Card
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openAiAssistant() }
                        .testTag("home_ai_assistant_banner"),
                    shape = RoundedCornerShape(24.dp),
                    elevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = if (isDark) {
                                        listOf(
                                            Color(0xFF2A2238).copy(alpha = 0.85f),
                                            Color(0xFF1E212D).copy(alpha = 0.9f)
                                        )
                                    } else {
                                        listOf(
                                            SpendWiseTheme.colors.lavender.copy(alpha = 0.35f),
                                            SpendWiseTheme.colors.softBlue.copy(alpha = 0.25f)
                                        )
                                    }
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
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
                                            text = "SpendWise AI Assistant",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = "Instant answers from verified numbers",
                                            fontSize = 11.sp,
                                            color = textSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SpendWiseTheme.colors.lavender,
                                    modifier = Modifier.clickable { viewModel.openAiAssistant() }
                                ) {
                                    Text(
                                        text = "Ask AI",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF171717),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick query pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val quickQueries = listOf("Where did money go?", "Food spend?", "How is budget?")
                                quickQueries.forEach { q ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDark) Color(0x33FFFFFF) else Color.White.copy(alpha = 0.8f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SpendWiseTheme.colors.glassBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.openAiAssistant()
                                                viewModel.askAssistant(q)
                                            }
                                    ) {
                                        Text(
                                            text = q,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textPrimary,
                                            maxLines = 1,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
            }

            // Smart Insights Section
            val smartInsights = viewModel.getSmartInsights()
            if (smartInsights.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Smart Insights",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SpendWiseTheme.colors.lavender.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "AI",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) SpendWiseTheme.colors.lavender else Color(0xFF6B4EE6),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(smartInsights) { insight ->
                            SmartInsightCard(
                                insight = insight,
                                onAction = {
                                    when (insight.type) {
                                        InsightType.BUDGET -> viewModel.navigateTo(AppScreen.BUDGETS)
                                        InsightType.GOAL -> viewModel.navigateTo(AppScreen.GOALS)
                                        InsightType.SPENDING -> viewModel.navigateTo(AppScreen.ANALYTICS)
                                        InsightType.ANOMALY -> viewModel.navigateTo(AppScreen.TRANSACTIONS)
                                        InsightType.SUMMARY -> viewModel.navigateTo(AppScreen.ANALYTICS)
                                    }
                                },
                                onCardClick = { viewModel.openAiAssistant() }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Recent Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "See All",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.lavender,
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(AppScreen.TRANSACTIONS) }
                            .testTag("home_see_all_transactions")
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Recent Transactions List (first 5)
            if (viewModel.transactions.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No transactions yet",
                        description = "Tap '+ Add Expense' above to start tracking your daily spend.",
                        buttonText = "Add Expense",
                        onButtonClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) }
                    )
                }
            } else {
                items(viewModel.transactions.take(5)) { tx ->
                    TransactionRow(
                        transaction = tx,
                        onClick = {
                            viewModel.selectedTransaction = tx
                            viewModel.navigateTo(AppScreen.TRANSACTION_DETAIL)
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(96.dp)) // Clearance for Bottom Navigation
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    iconColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val isDark = SpendWiseTheme.colors.isDark

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag(testTag)
    ) {
        GlassCard(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(22.dp),
            elevation = 3.dp,
            onClick = onClick
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = if (isDark) 0.25f else 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textPrimary,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun SmartInsightCard(
    insight: SmartInsight,
    onAction: () -> Unit,
    onCardClick: () -> Unit
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary
    val isDark = SpendWiseTheme.colors.isDark

    val accentColor = when (insight.severity) {
        InsightSeverity.WARNING -> SpendWiseTheme.colors.softCoral
        InsightSeverity.SUCCESS -> Color(0xFF34C759)
        InsightSeverity.INFO -> SpendWiseTheme.colors.softBlue
    }

    GlassCard(
        modifier = Modifier
            .width(260.dp)
            .clickable { onCardClick() }
            .testTag("smart_insight_${insight.id}"),
        shape = RoundedCornerShape(20.dp),
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
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
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (insight.actionText != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onAction() }
                    ) {
                        Text(
                            text = insight.actionText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SpendWiseTheme.colors.lavender
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = SpendWiseTheme.colors.lavender,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = insight.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = insight.description,
                fontSize = 12.sp,
                color = textSecondary,
                lineHeight = 16.sp,
                maxLines = 3
            )
        }
    }
}
