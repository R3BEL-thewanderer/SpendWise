package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.model.GoalItem
import com.example.state.ActiveModal
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.DigitalGulak
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassCard
import com.example.ui.components.LargeAmountInput
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SpendWiseTopBar
import com.example.ui.components.formatCurrency
import com.example.ui.components.getIconForType
import com.example.ui.theme.SpendWiseTheme

@Composable
fun GoalsOverviewScreen(viewModel: SpendWiseViewModel) {
    var selectedFilter by remember { mutableStateOf("All") } // "All", "In Progress", "Completed"
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    val filteredGoals = viewModel.goals.filter { g ->
        when (selectedFilter) {
            "In Progress" -> !g.isCompleted
            "Completed" -> g.isCompleted
            else -> true
        }
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
                            text = "Goals",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Small steps. Big plans.",
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.CREATE_GOAL) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (SpendWiseTheme.colors.isDark) Color(0x1AFFFFFF) else Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Goal", tint = textPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Hero Banner
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    elevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Turn your dreams\ninto reality",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Save consistently and achieve what matters to you.",
                                fontSize = 12.sp,
                                color = textSecondary,
                                lineHeight = 16.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(SpendWiseTheme.colors.lavender, SpendWiseTheme.colors.softBlue)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrackChanges,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "In Progress", "Completed").forEach { f ->
                        CategoryChip(
                            name = f,
                            isSelected = selectedFilter == f,
                            onClick = { selectedFilter = f },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (filteredGoals.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No goals found",
                        description = "Create a goal and watch your digital Gulak grow.",
                        buttonText = "Create Goal",
                        onButtonClick = { viewModel.navigateTo(AppScreen.CREATE_GOAL) }
                    )
                }
            } else {
                items(filteredGoals) { goal ->
                    GoalCardItem(
                        goal = goal,
                        onClick = {
                            viewModel.selectedGoal = goal
                            viewModel.navigateTo(AppScreen.GOAL_DETAIL)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(110.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { viewModel.navigateTo(AppScreen.CREATE_GOAL) },
            containerColor = SpendWiseTheme.colors.lavender,
            contentColor = Color(0xFF171717),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 96.dp)
                .size(56.dp)
                .testTag("fab_create_goal")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Goal", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun GoalCardItem(
    goal: GoalItem,
    onClick: () -> Unit
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary
    val color = Color(goal.colorHex)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_card_${goal.id}"),
        shape = RoundedCornerShape(22.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(color.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForType(goal.iconType),
                        contentDescription = goal.name,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${formatCurrency(goal.currentSavings)} / ${formatCurrency(goal.targetAmount)}",
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${goal.progressPercent}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpendWiseTheme.colors.lavender
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = goal.targetDate,
                        fontSize = 11.sp,
                        color = SpendWiseTheme.colors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = if (SpendWiseTheme.colors.isDark) Color(0x1FFFFFFF) else Color(0x14000000)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateGoalScreen(viewModel: SpendWiseViewModel) {
    var goalName by remember { mutableStateOf("New Laptop") }
    var targetAmountText by remember { mutableStateOf("80000") }
    var initialSavingsText by remember { mutableStateOf("25000") }
    var targetDate by remember { mutableStateOf("30 Jun 2026") }
    var category by remember { mutableStateOf("Electronics") }
    var selectedIcon by remember { mutableStateOf("laptop") }
    var selectedColorHex by remember { mutableLongStateOf(0xFF9CC9FF) }
    var description by remember { mutableStateOf("High performance laptop for development") }

    val icons = listOf("laptop", "beach", "bike", "shield", "food", "shopping")
    val colors = listOf(0xFF9CC9FF, 0xFFEF9C8D, 0xFFC9B8FF, 0xFFF4C7B5, 0xFFF5D98A, 0xFFB9DEC9)
    val scrollState = rememberScrollState()

    val targetAmt = targetAmountText.toDoubleOrNull() ?: 80000.0
    val currentAmt = initialSavingsText.toDoubleOrNull() ?: 25000.0
    val previewProgress = if (targetAmt > 0) (currentAmt / targetAmt).toFloat().coerceIn(0f, 1f) else 0f
    val previewPercent = (previewProgress * 100).toInt()

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
                title = "Create a Goal",
                onBackClick = { viewModel.navigateBack() }
            )

            // Live Preview Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Preview",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(selectedColorHex).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconForType(selectedIcon),
                                contentDescription = null,
                                tint = Color(selectedColorHex),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = goalName.ifBlank { "Goal Name" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpendWiseTheme.colors.textPrimary
                            )
                            Text(
                                text = "${formatCurrency(currentAmt)} / ${formatCurrency(targetAmt)}",
                                fontSize = 13.sp,
                                color = SpendWiseTheme.colors.textSecondary
                            )
                        }
                        Text(
                            text = "$previewPercent%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpendWiseTheme.colors.lavender
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { previewProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(selectedColorHex),
                        trackColor = if (SpendWiseTheme.colors.isDark) Color(0x1FFFFFFF) else Color(0x14000000)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Form Fields
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = goalName,
                        onValueChange = { goalName = it },
                        label = { Text("Goal Name") },
                        placeholder = { Text("E.g. Goa Trip, New Laptop") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = targetAmountText,
                        onValueChange = { targetAmountText = it.filter { c -> c.isDigit() } },
                        label = { Text("Target Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = initialSavingsText,
                        onValueChange = { initialSavingsText = it.filter { c -> c.isDigit() } },
                        label = { Text("Current Savings (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Target Date") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Choose an Icon",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        icons.forEach { iconName ->
                            val isSelected = selectedIcon == iconName
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) SpendWiseTheme.colors.lavender.copy(alpha = 0.35f)
                                        else if (SpendWiseTheme.colors.isDark) Color(0x1AFFFFFF) else Color(0x0F000000)
                                    )
                                    .clickable { selectedIcon = iconName },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getIconForType(iconName),
                                    contentDescription = iconName,
                                    tint = if (isSelected) SpendWiseTheme.colors.lavender else SpendWiseTheme.colors.textSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Choose a Color",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colors.forEach { hex ->
                            val isSelected = selectedColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .clickable { selectedColorHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        minLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Create Goal",
                onClick = {
                    viewModel.addGoal(
                        name = goalName,
                        targetAmount = targetAmt,
                        currentSavings = currentAmt,
                        targetDate = targetDate,
                        category = category,
                        iconType = selectedIcon,
                        colorHex = selectedColorHex,
                        description = description
                    )
                    viewModel.navigateTo(AppScreen.GOAL_DETAIL)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "submit_create_goal"
            )

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun GoalDetailScreen(viewModel: SpendWiseViewModel) {
    val goal = viewModel.selectedGoal ?: viewModel.goals.firstOrNull() ?: return
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
                title = goal.name,
                onBackClick = { viewModel.navigateBack() },
                onMoreClick = { viewModel.activeModal = ActiveModal.DELETE_GOAL }
            )

            // Goal Title & Description Subtitle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = goal.description.ifBlank { "For a better learning and productivity experience." },
                    fontSize = 13.sp,
                    color = textSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // THE SIGNATURE DIGITAL GULAK VESSEL
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DigitalGulak(
                    progress = goal.progress,
                    currentAmount = goal.currentSavings,
                    targetAmount = goal.targetAmount,
                    size = 250.dp,
                    showLabels = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Saved / Remaining / Target Date Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Saved", fontSize = 11.sp, color = textSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatCurrency(goal.currentSavings),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Remaining", fontSize = 11.sp, color = textSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatCurrency(goal.remainingAmount),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpendWiseTheme.colors.lavender
                        )
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Target Date", fontSize = 11.sp, color = textSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = goal.targetDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                LinearProgressIndicator(
                    progress = { goal.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(goal.colorHex),
                    trackColor = if (SpendWiseTheme.colors.isDark) Color(0x1FFFFFFF) else Color(0x14000000)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Contributions Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Contributions",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Recent Contributions List
            if (goal.contributions.isEmpty()) {
                Text(
                    text = "No contributions yet. Add money to watch the Gulak fill up!",
                    fontSize = 13.sp,
                    color = textSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            } else {
                goal.contributions.forEach { c ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34C759).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF34C759),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = c.note,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary
                                )
                                Text(
                                    text = c.date,
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }
                            Text(
                                text = "+ ${formatCurrency(c.amount)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34C759)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions: Add Money, Edit, Withdraw, Delete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrimaryButton(
                    text = "Add Money",
                    icon = Icons.Default.Add,
                    onClick = { viewModel.navigateTo(AppScreen.ADD_MONEY_GOAL) },
                    modifier = Modifier.weight(1.8f),
                    testTag = "goal_add_money_button"
                )

                SecondaryButton(
                    text = "Edit",
                    onClick = { viewModel.showToast("Editing ${goal.name}") },
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { viewModel.activeModal = ActiveModal.DELETE_GOAL },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF453A).copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF453A),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AddMoneyToGoalScreen(viewModel: SpendWiseViewModel) {
    val goal = viewModel.selectedGoal ?: viewModel.goals.firstOrNull() ?: return
    var contributionText by remember { mutableStateOf("5000") }

    val quickChips = listOf(500, 1000, 5000, 10000)
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    val addedAmt = contributionText.toDoubleOrNull() ?: 0.0
    val newSavings = goal.currentSavings + addedAmt
    val newProgress = if (goal.targetAmount > 0) (newSavings / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val newPercent = (newProgress * 100).toInt()
    val newRemaining = (goal.targetAmount - newSavings).coerceAtLeast(0.0)

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
                title = "Add Money",
                onBackClick = { viewModel.navigateBack() }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Every contribution brings you closer to your goal.",
                    fontSize = 13.sp,
                    color = textSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Goal Preview Pill
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(goal.colorHex).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForType(goal.iconType),
                            contentDescription = null,
                            tint = Color(goal.colorHex),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = goal.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                        Text(
                            text = "${formatCurrency(goal.currentSavings)} / ${formatCurrency(goal.targetAmount)}",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                    Text(
                        text = "${goal.progressPercent}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpendWiseTheme.colors.lavender
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Gulak Visual Preview
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DigitalGulak(
                    progress = newProgress,
                    currentAmount = newSavings,
                    targetAmount = goal.targetAmount,
                    size = 190.dp,
                    showLabels = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Input
            LargeAmountInput(
                amountText = contributionText,
                onAmountChange = { contributionText = it },
                label = "Contribution Amount",
                modifier = Modifier.testTag("add_money_amount_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Add Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickChips.forEach { chipAmt ->
                    CategoryChip(
                        name = "+ ₹$chipAmt",
                        isSelected = false,
                        onClick = {
                            val cur = contributionText.toDoubleOrNull() ?: 0.0
                            contributionText = (cur + chipAmt).toInt().toString()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress and Remaining Preview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "New Progress", fontSize = 11.sp, color = textSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${goal.progressPercent}% → $newPercent%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34C759)
                        )
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Remaining Amount", fontSize = 11.sp, color = textSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatCurrency(newRemaining),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpendWiseTheme.colors.lavender
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Add to Goal",
                onClick = {
                    if (addedAmt > 0) {
                        viewModel.addMoneyToGoal(goal.id, addedAmt)
                        viewModel.navigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "submit_add_to_goal"
            )

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
