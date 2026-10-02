package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.state.ActiveModal
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import com.example.state.ThemeMode
import com.example.ui.components.GlassCard
import com.example.ui.components.SpendWiseTopBar
import com.example.ui.components.formatCurrency
import com.example.ui.theme.SpendWiseTheme

@Composable
fun ProfileScreen(viewModel: SpendWiseViewModel) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary
    val isDark = SpendWiseTheme.colors.isDark

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
                            text = "Profile",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Your personal information and account details",
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x1AFFFFFF) else Color.White.copy(alpha = 0.8f))
                            .testTag("profile_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // User Avatar & Name
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(SpendWiseTheme.colors.softPeach, SpendWiseTheme.colors.lavender)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.userProfile.name.take(1).uppercase(),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF171717)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(SpendWiseTheme.colors.lavender)
                                .clickable { viewModel.showToast("Change avatar") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Edit Avatar",
                                tint = Color(0xFF171717),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = viewModel.userProfile.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = viewModel.userProfile.email,
                        fontSize = 13.sp,
                        color = textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Edit Profile Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isDark) Color(0x22FFFFFF) else Color.White.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpendWiseTheme.colors.glassBorder),
                        modifier = Modifier.clickable { viewModel.showToast("Edit profile enabled") }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Edit Profile", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Quick Metrics: Currency, Monthly Income, Monthly Budget
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(SpendWiseTheme.colors.softBlue.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("₹", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SpendWiseTheme.colors.softBlue)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Currency", fontSize = 11.sp, color = textSecondary)
                            Text(text = viewModel.userProfile.currency, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
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
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF34C759).copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF34C759), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Monthly Income", fontSize = 11.sp, color = textSecondary)
                            Text(text = formatCurrency(viewModel.monthlyIncome), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
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
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(SpendWiseTheme.colors.lavender.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = SpendWiseTheme.colors.lavender, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Monthly Budget", fontSize = 11.sp, color = textSecondary)
                            Text(text = formatCurrency(viewModel.userProfile.monthlyBudget), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Account Information
            item {
                Text(
                    text = "Account Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        ProfileInfoRow(icon = Icons.Default.Person, label = "Full Name", value = viewModel.userProfile.name)
                        Spacer(modifier = Modifier.height(14.dp))
                        ProfileInfoRow(icon = Icons.Default.Email, label = "Email Address", value = viewModel.userProfile.email)
                        Spacer(modifier = Modifier.height(14.dp))
                        ProfileInfoRow(icon = Icons.Default.Star, label = "Member Since", value = viewModel.userProfile.memberSince)
                        Spacer(modifier = Modifier.height(14.dp))
                        ProfileInfoRow(icon = Icons.Default.WorkspacePremium, label = "Account Type", value = viewModel.userProfile.accountType)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Upgrade to Premium Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    onClick = { viewModel.showToast("Premium features unlocked in demo") }
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(SpendWiseTheme.colors.warmYellow.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = SpendWiseTheme.colors.warmYellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Upgrade to Premium",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = "Get advanced analytics, unlimited budgets and more.",
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

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 14.sp, color = textSecondary, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = textSecondary.copy(alpha = 0.4f), modifier = Modifier.size(12.dp))
    }
}

@Composable
fun SettingsScreen(viewModel: SpendWiseViewModel) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

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
                    title = "Settings",
                    onBackClick = { viewModel.navigateBack() }
                )
                Text(
                    text = "Customize your experience on SpendWise.",
                    fontSize = 13.sp,
                    color = textSecondary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }

            // Appearance Section
            item {
                Text(text = "Appearance", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.padding(bottom = 8.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { viewModel.navigateTo(AppScreen.APPEARANCE) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (viewModel.themeMode == ThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = SpendWiseTheme.colors.lavender,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = when (viewModel.themeMode) {
                                ThemeMode.LIGHT -> "Light Mode"
                                ThemeMode.DARK -> "Dark Mode"
                                ThemeMode.SYSTEM -> "System Default"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Preferences Section
            item {
                Text(text = "Preferences", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.padding(bottom = 8.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsOptionRow(
                            icon = Icons.Default.AttachMoney,
                            title = "Currency",
                            subtitle = viewModel.userProfile.currency,
                            onClick = { viewModel.showToast("Default currency: INR ₹") }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingsOptionRow(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            subtitle = "Enabled",
                            onClick = { viewModel.showToast("Notifications enabled") }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingsOptionRow(
                            icon = Icons.Default.Smartphone,
                            title = "App Preferences",
                            subtitle = "Haptic feedback on",
                            onClick = { viewModel.showToast("App preferences saved") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Security Section
            item {
                Text(text = "Security", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.padding(bottom = 8.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsOptionRow(
                            icon = Icons.Default.Lock,
                            title = "Change Password",
                            subtitle = "Last changed 2 months ago",
                            onClick = { viewModel.navigateTo(AppScreen.FORGOT_PASSWORD) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingsOptionRow(
                            icon = Icons.Default.Security,
                            title = "Security Settings",
                            subtitle = "Biometric unlock active",
                            onClick = { viewModel.showToast("Biometric authentication active") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Data Section
            item {
                Text(text = "Data", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.padding(bottom = 8.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsOptionRow(
                            icon = Icons.Default.CloudDownload,
                            title = "Export Data",
                            subtitle = "Download CSV / PDF report",
                            onClick = { viewModel.showToast("Financial report exported to Downloads") }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingsOptionRow(
                            icon = Icons.Default.Delete,
                            title = "Delete Account",
                            subtitle = "Permanently remove your data",
                            titleColor = Color(0xFFFF453A),
                            onClick = { viewModel.activeModal = ActiveModal.DELETE_ACCOUNT }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Account / Logout Section
            item {
                Text(text = "Account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.padding(bottom = 8.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { viewModel.activeModal = ActiveModal.LOGOUT }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color(0xFFFF453A),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Log Out",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF453A),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SettingsOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color? = null
) {
    val textPrimary = titleColor ?: SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = titleColor ?: textSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = textSecondary)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = textSecondary.copy(alpha = 0.4f), modifier = Modifier.size(12.dp))
    }
}

@Composable
fun AppearanceScreen(viewModel: SpendWiseViewModel) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

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
                    title = "Appearance",
                    onBackClick = { viewModel.navigateBack() }
                )
                Text(
                    text = "Choose how SpendWise looks on your device.",
                    fontSize = 13.sp,
                    color = textSecondary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }

            // Light Mode Option
            item {
                ThemeOptionCard(
                    title = "Light Mode",
                    subtitle = "Clean, bright and minimal for everyday use.",
                    icon = Icons.Default.LightMode,
                    isSelected = viewModel.themeMode == ThemeMode.LIGHT,
                    onClick = {
                        viewModel.themeMode = ThemeMode.LIGHT
                        viewModel.showToast("Switched to Light Theme")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Dark Mode Option (with Popular badge)
            item {
                ThemeOptionCard(
                    title = "Dark Mode",
                    subtitle = "A modern dark experience with beautiful ambient glow.",
                    icon = Icons.Default.DarkMode,
                    badge = "Popular",
                    isSelected = viewModel.themeMode == ThemeMode.DARK,
                    onClick = {
                        viewModel.themeMode = ThemeMode.DARK
                        viewModel.showToast("Switched to Dark Theme")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // System Default Option
            item {
                ThemeOptionCard(
                    title = "System Default",
                    subtitle = "Automatically switch based on your system settings.",
                    icon = Icons.Default.Smartphone,
                    isSelected = viewModel.themeMode == ThemeMode.SYSTEM,
                    onClick = {
                        viewModel.themeMode = ThemeMode.SYSTEM
                        viewModel.showToast("Following System Settings")
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Live Theme Preview Box
            item {
                Text(
                    text = "Theme Preview",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total Balance", fontSize = 13.sp, color = textSecondary)
                            Text(text = "Active Theme", fontSize = 12.sp, color = SpendWiseTheme.colors.lavender)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "₹ 48,250", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF34C759).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "↑ 8% vs last month", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34C759))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) SpendWiseTheme.colors.lavender else SpendWiseTheme.colors.glassBorder,
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) SpendWiseTheme.colors.lavender.copy(alpha = 0.35f)
                        else SpendWiseTheme.colors.softSurface
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) SpendWiseTheme.colors.lavender else textSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SpendWiseTheme.colors.lavender)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF171717))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = textSecondary)
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = SpendWiseTheme.colors.lavender)
            )
        }
    }
}
