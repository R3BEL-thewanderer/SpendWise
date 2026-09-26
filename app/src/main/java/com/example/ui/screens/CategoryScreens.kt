package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CategoryItem
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SpendWiseTopBar
import com.example.ui.components.formatCurrency
import com.example.ui.components.getIconForType
import com.example.ui.theme.SpendWiseTheme

@Composable
fun CategoriesScreen(viewModel: SpendWiseViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    val filtered = viewModel.categories.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
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
                SpendWiseTopBar(
                    title = "Categories",
                    onBackClick = { viewModel.navigateBack() }
                )

                Text(
                    text = "Manage your spending categories",
                    fontSize = 13.sp,
                    color = textSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search categories...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = textSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("categories_search_input"),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SpendWiseTheme.colors.surface,
                        unfocusedContainerColor = SpendWiseTheme.colors.surface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            items(filtered) { cat ->
                val iconColor = Color(cat.colorHex)
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("category_row_${cat.id}"),
                    shape = RoundedCornerShape(20.dp),
                    onClick = {
                        viewModel.selectedCategory = cat
                        viewModel.navigateTo(AppScreen.EDIT_CATEGORY)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(iconColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconForType(cat.iconType),
                                contentDescription = cat.name,
                                tint = iconColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${formatCurrency(cat.spentAmount)} spent • ${cat.transactionCount} transactions",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(110.dp))
            }
        }

        // Floating Action Button to Add Category
        FloatingActionButton(
            onClick = { viewModel.navigateTo(AppScreen.ADD_CATEGORY) },
            containerColor = SpendWiseTheme.colors.lavender,
            contentColor = Color(0xFF171717),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 96.dp)
                .size(56.dp)
                .testTag("fab_add_category")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Category", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun AddCategoryScreen(viewModel: SpendWiseViewModel) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("shopping") }
    var selectedColor by remember { mutableLongStateOf(0xFFEF9C8D) }

    val icons = listOf("food", "shopping", "transport", "bills", "entertainment", "health", "education", "laptop", "beach", "bike")
    val colors = listOf(0xFF9CC9FF, 0xFFEF9C8D, 0xFFC9B8FF, 0xFFF4C7B5, 0xFFF5D98A, 0xFFB9DEC9)
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
                title = "Add Category",
                onBackClick = { viewModel.navigateBack() }
            )

            // Header Sphere with Selected Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(selectedColor).copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForType(selectedIcon),
                        contentDescription = null,
                        tint = Color(selectedColor),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Category Name") },
                        placeholder = { Text("E.g. Groceries, Gym, Streaming") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_category_name_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Choose an Icon",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        icons.take(5).forEach { ic ->
                            val isSel = selectedIcon == ic
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) SpendWiseTheme.colors.lavender.copy(alpha = 0.35f) else Color(0x0F000000))
                                    .clickable { selectedIcon = ic },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getIconForType(ic),
                                    contentDescription = ic,
                                    tint = if (isSel) SpendWiseTheme.colors.lavender else SpendWiseTheme.colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        icons.drop(5).forEach { ic ->
                            val isSel = selectedIcon == ic
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) SpendWiseTheme.colors.lavender.copy(alpha = 0.35f) else Color(0x0F000000))
                                    .clickable { selectedIcon = ic },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getIconForType(ic),
                                    contentDescription = ic,
                                    tint = if (isSel) SpendWiseTheme.colors.lavender else SpendWiseTheme.colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
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
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colors.forEach { col ->
                            val isSel = selectedColor == col
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(col))
                                    .clickable { selectedColor = col },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Create Category",
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addCategory(name, selectedIcon, selectedColor)
                        viewModel.navigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "submit_add_category"
            )

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun EditCategoryScreen(viewModel: SpendWiseViewModel) {
    val cat = viewModel.selectedCategory ?: viewModel.categories.firstOrNull() ?: return
    var name by remember { mutableStateOf(cat.name) }
    var selectedIcon by remember { mutableStateOf(cat.iconType) }
    var selectedColor by remember { mutableLongStateOf(cat.colorHex) }

    val icons = listOf("food", "shopping", "transport", "bills", "entertainment", "health", "education", "laptop", "beach", "bike")
    val colors = listOf(0xFF9CC9FF, 0xFFEF9C8D, 0xFFC9B8FF, 0xFFF4C7B5, 0xFFF5D98A, 0xFFB9DEC9)
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
                title = "Edit Category",
                onBackClick = { viewModel.navigateBack() }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(selectedColor).copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForType(selectedIcon),
                        contentDescription = null,
                        tint = Color(selectedColor),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Category Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(text = "Choose an Icon", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SpendWiseTheme.colors.textSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        icons.take(5).forEach { ic ->
                            val isSel = selectedIcon == ic
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) SpendWiseTheme.colors.lavender.copy(alpha = 0.35f) else Color(0x0F000000))
                                    .clickable { selectedIcon = ic },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getIconForType(ic),
                                    contentDescription = ic,
                                    tint = if (isSel) SpendWiseTheme.colors.lavender else SpendWiseTheme.colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(text = "Choose a Color", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SpendWiseTheme.colors.textSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colors.forEach { col ->
                            val isSel = selectedColor == col
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(col))
                                    .clickable { selectedColor = col },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Save Changes",
                onClick = {
                    val updated = cat.copy(name = name, iconType = selectedIcon, colorHex = selectedColor)
                    viewModel.updateCategory(updated)
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                testTag = "save_category_changes"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    viewModel.deleteCategory(cat)
                    viewModel.navigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Delete Category",
                    color = Color(0xFFFF453A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
