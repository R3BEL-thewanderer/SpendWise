package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.TransactionItem
import com.example.model.TransactionType
import com.example.state.ActiveModal
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SpendWiseTopBar
import com.example.ui.components.TransactionRow
import com.example.ui.components.formatCurrency
import com.example.ui.components.getIconForType
import com.example.ui.theme.SpendWiseTheme

@Composable
fun TransactionsScreen(viewModel: SpendWiseViewModel) {
    var selectedTab by remember { mutableStateOf("All") } // "All", "Expenses", "Income"
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    // Filter logic
    val filteredList = viewModel.transactions.filter { tx ->
        val matchesTab = when (selectedTab) {
            "Expenses" -> tx.type == TransactionType.EXPENSE
            "Income" -> tx.type == TransactionType.INCOME
            else -> true
        }
        val matchesSearch = viewModel.transactionSearchQuery.isBlank() ||
                tx.title.contains(viewModel.transactionSearchQuery, ignoreCase = true) ||
                tx.category.contains(viewModel.transactionSearchQuery, ignoreCase = true)
        val matchesCategory = viewModel.transactionFilter.selectedCategory == "All" ||
                tx.category.equals(viewModel.transactionFilter.selectedCategory, ignoreCase = true)

        matchesTab && matchesSearch && matchesCategory
    }

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
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Transactions",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Your complete transaction history",
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.activeModal = ActiveModal.TRANSACTION_FILTER },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (SpendWiseTheme.colors.isDark) Color(0x1AFFFFFF) else Color.White.copy(alpha = 0.8f))
                            .testTag("transactions_filter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = viewModel.transactionSearchQuery,
                    onValueChange = { viewModel.transactionSearchQuery = it },
                    placeholder = { Text("Search transactions...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = textSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transactions_search_input"),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SpendWiseTheme.colors.surface,
                        unfocusedContainerColor = SpendWiseTheme.colors.surface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Pills: All, Expenses, Income
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Expenses", "Income").forEach { tab ->
                        CategoryChip(
                            name = tab,
                            isSelected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (filteredList.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No transactions found",
                        description = "Try adjusting your filters or add a new transaction.",
                        buttonText = "Add Expense",
                        onButtonClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) }
                    )
                }
            } else {
                // Group by Today / Yesterday / Earlier
                val todayList = filteredList.filter { it.date.equals("Today", ignoreCase = true) }
                val yesterdayList = filteredList.filter { it.date.equals("Yesterday", ignoreCase = true) }
                val otherList = filteredList.filter { !it.date.equals("Today", ignoreCase = true) && !it.date.equals("Yesterday", ignoreCase = true) }

                if (todayList.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Today", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                            val todaySpent = todayList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                            Text(text = "- ${formatCurrency(todaySpent)}", fontSize = 13.sp, color = textSecondary)
                        }
                    }
                    items(todayList) { tx ->
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

                if (yesterdayList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Yesterday", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                            val yesterdaySpent = yesterdayList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                            Text(text = "- ${formatCurrency(yesterdaySpent)}", fontSize = 13.sp, color = textSecondary)
                        }
                    }
                    items(yesterdayList) { tx ->
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

                if (otherList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Earlier",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(otherList) { tx ->
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
            }

            item {
                Spacer(modifier = Modifier.height(110.dp))
            }
        }

        // Floating Action Button (+)
        FloatingActionButton(
            onClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) },
            containerColor = SpendWiseTheme.colors.lavender,
            contentColor = Color(0xFF171717),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 96.dp)
                .size(56.dp)
                .testTag("fab_add_transaction")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Expense", modifier = Modifier.size(28.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionFilterDialog(
    viewModel: SpendWiseViewModel,
    onDismiss: () -> Unit
) {
    var selectedDateRange by remember { mutableStateOf(viewModel.transactionFilter.dateRange) }
    var selectedCategory by remember { mutableStateOf(viewModel.transactionFilter.selectedCategory) }
    var selectedType by remember { mutableStateOf(viewModel.transactionFilter.type) }
    var selectedMethod by remember { mutableStateOf(viewModel.transactionFilter.paymentMethod) }

    val dateRanges = listOf("Last 7 days", "Last 30 days", "This Month", "Custom")
    val categories = listOf("All", "Food & Dining", "Shopping", "Transport", "Bills & Utilities", "Entertainment", "Health")
    val types = listOf("All", "Expenses", "Income")
    val methods = listOf("All", "UPI", "Credit Card", "Debit Card", "Cash", "Net Banking")

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .clip(RoundedCornerShape(28.dp))
                .testTag("transaction_filter_modal"),
            shape = RoundedCornerShape(28.dp),
            color = if (SpendWiseTheme.colors.isDark) Color(0xFF1A1D23) else Color.White,
            shadowElevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Transactions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpendWiseTheme.colors.textPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.FilterList, contentDescription = "Close", tint = SpendWiseTheme.colors.textSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Date Range", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SpendWiseTheme.colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    dateRanges.forEach { range ->
                        CategoryChip(
                            name = range,
                            isSelected = selectedDateRange == range,
                            onClick = { selectedDateRange = range }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Categories", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SpendWiseTheme.colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        CategoryChip(
                            name = cat,
                            icon = if (cat != "All") getIconForType(cat) else null,
                            isSelected = selectedCategory == cat,
                            onClick = { selectedCategory = cat }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Transaction Type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SpendWiseTheme.colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { t ->
                        CategoryChip(
                            name = t,
                            isSelected = selectedType == t,
                            onClick = { selectedType = t },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Payment Method", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SpendWiseTheme.colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    methods.forEach { m ->
                        CategoryChip(
                            name = m,
                            isSelected = selectedMethod == m,
                            onClick = { selectedMethod = m }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = "Reset",
                        onClick = {
                            viewModel.transactionFilter = com.example.state.TransactionFilter()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "Apply Filters",
                        onClick = {
                            viewModel.transactionFilter = com.example.state.TransactionFilter(
                                dateRange = selectedDateRange,
                                selectedCategory = selectedCategory,
                                type = selectedType,
                                paymentMethod = selectedMethod
                            )
                            onDismiss()
                        },
                        showArrow = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionDetailScreen(viewModel: SpendWiseViewModel) {
    val tx = viewModel.selectedTransaction ?: viewModel.transactions.firstOrNull() ?: return
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary
    val isExpense = tx.type == TransactionType.EXPENSE
    val iconBgColor = Color(tx.colorHex)

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
                title = "",
                onBackClick = { viewModel.navigateBack() },
                onMoreClick = { viewModel.activeModal = ActiveModal.DELETE_TRANSACTION }
            )

            // Glowing Hero Sphere
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    iconBgColor.copy(alpha = 0.35f),
                                    SpendWiseTheme.colors.lavender.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForType(tx.iconType),
                            contentDescription = tx.category,
                            tint = iconBgColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Title & Amount
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tx.category,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = (if (isExpense) "- " else "+ ") + formatCurrency(tx.amount),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) textPrimary else Color(0xFF34C759)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${tx.title} • ${tx.date}, ${tx.time}",
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details List
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    DetailRowItem(
                        icon = getIconForType(tx.iconType),
                        label = "Category",
                        value = tx.category
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    DetailRowItem(
                        icon = Icons.Default.CreditCard,
                        label = "Payment Method",
                        value = tx.paymentMethod
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    DetailRowItem(
                        icon = Icons.Default.Notes,
                        label = "Notes",
                        value = if (tx.notes.isNotBlank()) tx.notes else "No notes attached"
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    DetailRowItem(
                        icon = Icons.Default.LocalOffer,
                        label = "Tags",
                        value = tx.tags.joinToString(", ")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Spending Insights Card
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
                        Text(
                            text = "Spending Insights",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your ${tx.category.lowercase()} spending is 12% higher than last month.",
                        fontSize = 13.sp,
                        color = textSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Monthly mini bar chart (Jan - Jun)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val months = listOf("Jan" to 30.dp, "Feb" to 58.dp, "Mar" to 42.dp, "Apr" to 38.dp, "May" to 48.dp, "Jun" to 34.dp)
                        months.forEachIndexed { i, m ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(m.second)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (i == 1) iconBgColor
                                            else iconBgColor.copy(alpha = 0.3f)
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = m.first, fontSize = 11.sp, color = textSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = "Edit Transaction",
                    icon = Icons.Default.Edit,
                    onClick = {
                        if (isExpense) {
                            viewModel.navigateTo(AppScreen.EDIT_EXPENSE)
                        } else {
                            viewModel.navigateTo(AppScreen.EDIT_INCOME)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "detail_edit_button"
                )

                IconButton(
                    onClick = { viewModel.activeModal = ActiveModal.DELETE_TRANSACTION },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF453A).copy(alpha = 0.15f))
                        .testTag("detail_delete_button")
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
private fun DetailRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
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
}
