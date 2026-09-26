package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.state.ActiveModal
import com.example.state.SpendWiseViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.GlassCard
import com.example.ui.components.LargeAmountInput
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SpendWiseTopBar
import com.example.ui.components.getIconForType
import com.example.ui.theme.SpendWiseTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreen(viewModel: SpendWiseViewModel) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food & Dining") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Today") }
    var paymentMethod by remember { mutableStateOf("UPI") }
    var notes by remember { mutableStateOf("") }
    val tags = remember { mutableStateListOf("Essentials") }

    val categories = listOf("Food & Dining", "Shopping", "Transport", "Bills & Utilities", "Entertainment", "Health")
    val paymentMethods = listOf("UPI", "HDFC Credit Card", "Debit Card", "Cash")
    val availableTags = listOf("Essentials", "Personal", "Work", "College", "Weekend")

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
                title = "Add Expense",
                onBackClick = { viewModel.navigateBack() }
            )

            // Glowing Sphere with receipt/expense icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    SpendWiseTheme.colors.softCoral.copy(alpha = 0.35f),
                                    SpendWiseTheme.colors.softPeach.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = SpendWiseTheme.colors.softCoral,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Big Focused Amount
            LargeAmountInput(
                amountText = amountText,
                onAmountChange = { amountText = it },
                label = "Enter expense amount",
                modifier = Modifier.testTag("add_expense_amount_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form container
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            CategoryChip(
                                name = cat,
                                icon = getIconForType(cat),
                                isSelected = selectedCategory == cat,
                                onClick = { selectedCategory = cat }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("E.g. Dinner with friends") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_expense_desc_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SpendWiseTheme.colors.surface,
                            unfocusedContainerColor = SpendWiseTheme.colors.surface
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Payment Method",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        paymentMethods.forEach { method ->
                            CategoryChip(
                                name = method,
                                isSelected = paymentMethod == method,
                                onClick = { paymentMethod = method }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tags",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTags.forEach { tag ->
                            val isSelected = tags.contains(tag)
                            CategoryChip(
                                name = tag,
                                isSelected = isSelected,
                                onClick = {
                                    if (isSelected) tags.remove(tag) else tags.add(tag)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (Optional)") },
                        placeholder = { Text("Add any extra details") },
                        leadingIcon = {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_expense_notes_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SpendWiseTheme.colors.surface,
                            unfocusedContainerColor = SpendWiseTheme.colors.surface
                        ),
                        minLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Save Expense",
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 1299.0
                    viewModel.addExpense(
                        amount = amt,
                        title = description.ifBlank { selectedCategory },
                        category = selectedCategory,
                        date = date,
                        paymentMethod = paymentMethod,
                        tags = tags.toList(),
                        notes = notes
                    )
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "save_expense_button"
            )

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditExpenseScreen(viewModel: SpendWiseViewModel) {
    val tx = viewModel.selectedTransaction ?: viewModel.transactions.firstOrNull() ?: return
    var amountText by remember { mutableStateOf(tx.amount.toInt().toString()) }
    var selectedCategory by remember { mutableStateOf(tx.category) }
    var description by remember { mutableStateOf(tx.title) }
    var paymentMethod by remember { mutableStateOf(tx.paymentMethod) }
    var notes by remember { mutableStateOf(tx.notes) }
    val tags = remember { mutableStateListOf(*tx.tags.toTypedArray()) }

    val categories = listOf("Food & Dining", "Shopping", "Transport", "Bills & Utilities", "Entertainment", "Health")
    val paymentMethods = listOf("UPI", "HDFC Credit Card", "Debit Card", "Cash")
    val availableTags = listOf("Essentials", "Personal", "Work", "College", "Weekend")

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
                title = "Edit Expense",
                onBackClick = { viewModel.navigateBack() }
            )

            LargeAmountInput(
                amountText = amountText,
                onAmountChange = { amountText = it },
                label = "Edit amount",
                modifier = Modifier.testTag("edit_expense_amount_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            CategoryChip(
                                name = cat,
                                icon = getIconForType(cat),
                                isSelected = selectedCategory == cat,
                                onClick = { selectedCategory = cat }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Payment Method",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        paymentMethods.forEach { method ->
                            CategoryChip(
                                name = method,
                                isSelected = paymentMethod == method,
                                onClick = { paymentMethod = method }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        minLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Save Changes",
                onClick = {
                    val updated = tx.copy(
                        amount = amountText.toDoubleOrNull() ?: tx.amount,
                        title = description,
                        category = selectedCategory,
                        paymentMethod = paymentMethod,
                        notes = notes,
                        tags = tags.toList()
                    )
                    viewModel.updateTransaction(updated)
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "save_expense_changes_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { viewModel.activeModal = ActiveModal.DELETE_TRANSACTION },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("delete_expense_button")
            ) {
                Text(
                    text = "Delete Transaction",
                    color = Color(0xFFFF453A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun AddIncomeScreen(viewModel: SpendWiseViewModel) {
    var amountText by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("Salary") }
    var category by remember { mutableStateOf("Income") }
    var notes by remember { mutableStateOf("") }

    val sources = listOf("Salary", "Freelance", "Business", "Investment", "Other")
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
                title = "Add Income",
                onBackClick = { viewModel.navigateBack() }
            )

            // Glowing Sphere with green upward arrow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF34C759).copy(alpha = 0.3f),
                                    SpendWiseTheme.colors.softMint.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            LargeAmountInput(
                amountText = amountText,
                onAmountChange = { amountText = it },
                label = "Enter income amount",
                modifier = Modifier.testTag("add_income_amount_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Income Source",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sources.take(3).forEach { s ->
                            CategoryChip(
                                name = s,
                                isSelected = source == s,
                                onClick = { source = s },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sources.drop(3).forEach { s ->
                            CategoryChip(
                                name = s,
                                isSelected = source == s,
                                onClick = { source = s },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (Optional)") },
                        placeholder = { Text("E.g. Monthly salary, consulting payout") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_income_notes_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SpendWiseTheme.colors.surface,
                            unfocusedContainerColor = SpendWiseTheme.colors.surface
                        ),
                        minLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Save Income",
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 45000.0
                    viewModel.addIncome(
                        amount = amt,
                        source = source,
                        date = "Today",
                        category = category,
                        notes = notes
                    )
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "save_income_button"
            )

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun EditIncomeScreen(viewModel: SpendWiseViewModel) {
    val tx = viewModel.selectedTransaction ?: viewModel.transactions.firstOrNull() ?: return
    var amountText by remember { mutableStateOf(tx.amount.toInt().toString()) }
    var source by remember { mutableStateOf(tx.title) }
    var notes by remember { mutableStateOf(tx.notes) }

    val sources = listOf("Salary", "Freelance", "Business", "Investment", "Other")
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
                title = "Edit Income",
                onBackClick = { viewModel.navigateBack() }
            )

            LargeAmountInput(
                amountText = amountText,
                onAmountChange = { amountText = it },
                label = "Edit income amount",
                modifier = Modifier.testTag("edit_income_amount_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Income Source",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sources.take(3).forEach { s ->
                            CategoryChip(
                                name = s,
                                isSelected = source == s,
                                onClick = { source = s },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        minLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Save Changes",
                onClick = {
                    val updated = tx.copy(
                        amount = amountText.toDoubleOrNull() ?: tx.amount,
                        title = source,
                        notes = notes
                    )
                    viewModel.updateTransaction(updated)
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "save_income_changes_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { viewModel.activeModal = ActiveModal.DELETE_INCOME },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Delete Income",
                    color = Color(0xFFFF453A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
