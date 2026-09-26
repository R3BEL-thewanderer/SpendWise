package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.AppScreen
import com.example.ui.theme.SpendWiseTheme

@Composable
fun AppBottomNavigation(
    currentScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = SpendWiseTheme.colors.isDark

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            elevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    isSelected = currentScreen == AppScreen.HOME,
                    onClick = { onTabSelected(AppScreen.HOME) },
                    testTag = "tab_home"
                )
                BottomNavItem(
                    label = "Transactions",
                    selectedIcon = Icons.Filled.ReceiptLong,
                    unselectedIcon = Icons.Outlined.ReceiptLong,
                    isSelected = currentScreen == AppScreen.TRANSACTIONS,
                    onClick = { onTabSelected(AppScreen.TRANSACTIONS) },
                    testTag = "tab_transactions"
                )
                BottomNavItem(
                    label = "Budgets",
                    selectedIcon = Icons.Filled.PieChart,
                    unselectedIcon = Icons.Outlined.PieChart,
                    isSelected = currentScreen == AppScreen.BUDGETS,
                    onClick = { onTabSelected(AppScreen.BUDGETS) },
                    testTag = "tab_budgets"
                )
                BottomNavItem(
                    label = "Goals",
                    selectedIcon = Icons.Filled.EmojiEvents,
                    unselectedIcon = Icons.Outlined.EmojiEvents,
                    isSelected = currentScreen == AppScreen.GOALS,
                    onClick = { onTabSelected(AppScreen.GOALS) },
                    testTag = "tab_goals"
                )
                BottomNavItem(
                    label = "Analytics",
                    selectedIcon = Icons.Filled.AutoGraph,
                    unselectedIcon = Icons.Outlined.AutoGraph,
                    isSelected = currentScreen == AppScreen.ANALYTICS,
                    onClick = { onTabSelected(AppScreen.ANALYTICS) },
                    testTag = "tab_analytics"
                )
                BottomNavItem(
                    label = "Profile",
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    isSelected = currentScreen == AppScreen.PROFILE,
                    onClick = { onTabSelected(AppScreen.PROFILE) },
                    testTag = "tab_profile"
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val isDark = SpendWiseTheme.colors.isDark
    val activeColor = SpendWiseTheme.colors.textPrimary
    val inactiveColor = SpendWiseTheme.colors.textMuted

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "navScale"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .scale(animatedScale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .padding(vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        if (isDark) Color(0x33FFFFFF) else Color(0x1F171717)
                    } else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) activeColor else inactiveColor,
            maxLines = 1
        )
    }
}

@Composable
fun SpendWiseTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (SpendWiseTheme.colors.isDark) Color(0x1AFFFFFF) else Color.White.copy(alpha = 0.8f)
                    )
                    .testTag("top_bar_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            modifier = Modifier.weight(1f)
        )

        if (trailingContent != null) {
            trailingContent()
        } else if (onMoreClick != null) {
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (SpendWiseTheme.colors.isDark) Color(0x1AFFFFFF) else Color.White.copy(alpha = 0.8f)
                    )
                    .testTag("top_bar_more")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
