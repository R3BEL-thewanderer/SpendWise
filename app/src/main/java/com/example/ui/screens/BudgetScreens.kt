package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BudgetItem
import com.example.model.CategoryAllocation
import com.example.model.TransactionItem
import com.example.model.TransactionType
import com.example.state.ActiveModal
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassCard
import com.example.ui.components.MultiCategoryDonut
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SingleDonutProgress
import com.example.ui.components.SpendWiseTopBar
import com.example.ui.components.TransactionRow
import com.example.ui.components.formatCurrency
import com.example.ui.components.getIconForType
import com.example.ui.theme.SpendWiseTheme

@Composable
fun BudgetsOverviewScreen(viewModel: SpendWiseViewModel) {
    val budget = viewModel.selectedBudget ?: viewModel.budgets.firstOrNull()
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        if (budget == null) {
            EmptyStateView(
                title = "No budgets yet",
                description = "Create your monthly spending plan to stay on track.",
                buttonText = "Create Budget",
                onButtonClick = { viewModel.navigateTo(AppScreen.CREATE_BUDGET) }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {
                // Top Bar / Title with month dropdown
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Budget",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )

                        // Month pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (SpendWiseTheme.colors.isDark) Color(0x22FFFFFF) else Color.White.copy(alpha = 0.8f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SpendWiseTheme.colors.glassBorder),
                            modifier = Modifier.clickable {
                                viewModel.showToast("Current period: ${budget.month}")
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = budget.month,
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

                // Donut Chart Glass Container
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_donut_card"),
                        shape = RoundedCornerShape(28.dp),
                        elevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MultiCategoryDonut(
                                spent = budget.spent,
                                totalLimit = budget.totalLimit,
                                allocations = budget.allocations
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Legend Grid
                            Column(modifier = Modifier.fillMaxWidth()) {
                                budget.allocations.take(4).forEach { alloc ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(alloc.colorHex))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = alloc.categoryName,
                                                fontSize = 13.sp,
                                                color = textSecondary
                                            )
                                        }
                                        Text(
                                            text = formatCurrency(alloc.amount),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Category Budgets Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category Budgets",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Manage",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SpendWiseTheme.colors.lavender,
                            modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.EDIT_BUDGET) }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Category Budgets List
                items(budget.allocations) { alloc ->
                    val catSpent = (alloc.amount * (alloc.percentage / 100f * 3.5)).coerceAtMost(alloc.amount)
                    val catProgress = (catSpent / alloc.amount).toFloat().coerceIn(0f, 1f)
                    val catPercent = (catProgress * 100).toInt()

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("budget_item_${alloc.categoryName}"),
                        shape = RoundedCornerShape(20.dp),
                        onClick = {
                            viewModel.selectedBudget = budget
                            viewModel.navigateTo(AppScreen.BUDGET_DETAIL)
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(alloc.colorHex).copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getIconForType(alloc.iconType),
                                        contentDescription = alloc.categoryName,
                                        tint = Color(alloc.colorHex),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alloc.categoryName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "${formatCurrency(catSpent)} / ${formatCurrency(alloc.amount)}",
                                        fontSize = 12.sp,
                                        color = textSecondary
                                    )
                                }
                                Text(
                                    text = "$catPercent%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = textSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { catProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(alloc.colorHex),
                                trackColor = if (SpendWiseTheme.colors.isDark) Color(0x1FFFFFFF) else Color(0x14000000)
                            )
                        }
                    }
                }

                // Stay on Track Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
                                    .background(Color(0xFF34C759).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF34C759),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Stay on Track",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Text(
                                    text = "You're 12% under your planned spending. Great job!",
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(110.dp))
                }
            }
        }

        // FAB to Create Budget
        FloatingActionButton(
            onClick = { viewModel.navigateTo(AppScreen.CREATE_BUDGET) },
            containerColor = SpendWiseTheme.colors.lavender,
            contentColor = Color(0xFF171717),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 96.dp)
                .size(56.dp)
                .testTag("fab_create_budget")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Budget", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun CreateBudgetScreen(viewModel: SpendWiseViewModel) {
    var budgetName by remember { mutableStateOf("March Budget") }
    var totalLimitText by remember { mutableStateOf("30000") }
    var month by remember { mutableStateOf("March 2025") }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            SpendWiseTopBar(
                title = "Create Budget",
                onBackClick = { viewModel.navigateBack() }
            )

            // Header Hero Banner
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SpendWiseTheme.colors.lavender.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = SpendWiseTheme.colors.lavender,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Set Monthly Budget",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpendWiseTheme.colors.textPrimary
                        )
                        Text(
                            text = "Stay on track and build smarter habits.",
                            fontSize = 12.sp,
                            color = SpendWiseTheme.colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = budgetName,
                        onValueChange = { budgetName = it },
                        label = { Text("Budget Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = totalLimitText,
                        onValueChange = { totalLimitText = it.filter { c -> c.isDigit() } },
                        label = { Text("Total Monthly Limit (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it },
                        label = { Text("Month") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Create Budget",
                onClick = {
                    val limit = totalLimitText.toDoubleOrNull() ?: 30000.0
                    val allocations = listOf(
                        CategoryAllocation("Food & Drinks", 25, limit * 0.25, 0xFF9CC9FF, "food"),
                        CategoryAllocation("Shopping", 20, limit * 0.20, 0xFFEF9C8D, "shopping"),
                        CategoryAllocation("Transport", 15, limit * 0.15, 0xFFC9B8FF, "transport"),
                        CategoryAllocation("Bills & Utilities", 20, limit * 0.20, 0xFFF5D98A, "bills"),
                        CategoryAllocation("Entertainment", 10, limit * 0.10, 0xFFB9DEC9, "entertainment"),
                        CategoryAllocation("Other", 10, limit * 0.10, 0xFFF4C7B5, "other")
                    )
                    viewModel.createBudget(budgetName, limit, month, allocations)
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "submit_create_budget"
            )

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun BudgetDetailScreen(viewModel: SpendWiseViewModel) {
    val budget = viewModel.selectedBudget ?: viewModel.budgets.firstOrNull() ?: return
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            SpendWiseTopBar(
                title = "Shopping Budget",
                onBackClick = { viewModel.navigateBack() },
                onMoreClick = { viewModel.navigateTo(AppScreen.EDIT_BUDGET) }
            )

            // Header Icon & Category
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SpendWiseTheme.colors.softCoral.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForType("shopping"),
                        contentDescription = null,
                        tint = SpendWiseTheme.colors.softCoral,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Shopping", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "March 2025 • ", fontSize = 13.sp, color = textSecondary)
                    Text(text = "On Track", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34C759))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Donut Progress Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SingleDonutProgress(
                        percentage = 68,
                        label = "used",
                        color = SpendWiseTheme.colors.softCoral,
                        size = 130.dp
                    )

                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SpendWiseTheme.colors.softPeach))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Monthly Limit", fontSize = 12.sp, color = textSecondary)
                        }
                        Text(text = "₹ 6,000", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SpendWiseTheme.colors.softCoral))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Amount Spent", fontSize = 12.sp, color = textSecondary)
                        }
                        Text(text = "₹ 4,080", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF34C759)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Remaining", fontSize = 12.sp, color = textSecondary)
                        }
                        Text(text = "₹ 1,920", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34C759))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Daily Spending Trend Bar Chart
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Spending Trend", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Text(text = "Daily", fontSize = 12.sp, color = SpendWiseTheme.colors.lavender)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val heights = listOf(15.dp, 35.dp, 20.dp, 45.dp, 10.dp, 50.dp, 25.dp, 40.dp)
                        heights.forEachIndexed { i, h ->
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(h)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (i == 5) SpendWiseTheme.colors.softCoral
                                        else SpendWiseTheme.colors.softCoral.copy(alpha = 0.35f)
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = "Edit Budget",
                    icon = Icons.Default.Edit,
                    onClick = { viewModel.navigateTo(AppScreen.EDIT_BUDGET) },
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { viewModel.activeModal = ActiveModal.DELETE_BUDGET },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF453A).copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF453A),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun EditBudgetScreen(viewModel: SpendWiseViewModel) {
    val budget = viewModel.selectedBudget ?: viewModel.budgets.firstOrNull() ?: return
    var totalLimitText by remember { mutableStateOf(budget.totalLimit.toInt().toString()) }
    var selectedAlert by remember { mutableIntStateOf(budget.alertThresholdPercent) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            SpendWiseTopBar(
                title = "Edit Budget",
                onBackClick = { viewModel.navigateBack() }
            )

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Update Budget",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpendWiseTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Modify your monthly limit and alert settings.",
                        fontSize = 13.sp,
                        color = SpendWiseTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = totalLimitText,
                        onValueChange = { totalLimitText = it.filter { c -> c.isDigit() } },
                        label = { Text("Total Monthly Limit (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Spending Alerts Threshold",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(75, 90, 100).forEach { th ->
                            CategoryChip(
                                name = "$th%",
                                isSelected = selectedAlert == th,
                                onClick = { selectedAlert = th },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Save Changes",
                onClick = {
                    val newLimit = totalLimitText.toDoubleOrNull() ?: budget.totalLimit
                    viewModel.updateBudget(
                        budget.copy(totalLimit = newLimit, alertThresholdPercent = selectedAlert)
                    )
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "save_budget_changes"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { viewModel.activeModal = ActiveModal.DELETE_BUDGET },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Delete Budget",
                    color = Color(0xFFFF453A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
