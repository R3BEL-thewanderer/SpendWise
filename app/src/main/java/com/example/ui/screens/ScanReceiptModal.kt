package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.state.ActiveModal
import com.example.state.SpendWiseViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.SpendWiseTheme

@Composable
fun ScanReceiptModal(
    viewModel: SpendWiseViewModel,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 110f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = if (SpendWiseTheme.colors.isDark) Color(0xFF1E2127) else Color.White,
            shadowElevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scan Receipt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpendWiseTheme.colors.textPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SpendWiseTheme.colors.textSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated Viewfinder Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (SpendWiseTheme.colors.isDark) Color(0xFF131518) else Color(0xFFF2F0EB))
                        .border(1.dp, SpendWiseTheme.colors.lavender.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = SpendWiseTheme.colors.textSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Position receipt within frame",
                            fontSize = 12.sp,
                            color = SpendWiseTheme.colors.textSecondary
                        )
                    }

                    // Scanning Laser Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .offset(y = (laserY - 55).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        SpendWiseTheme.colors.lavender,
                                        SpendWiseTheme.colors.softBlue,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Detected Receipt Details
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Detected Item",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SpendWiseTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Blue Tokai Coffee Roasters",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpendWiseTheme.colors.textPrimary
                            )
                            Text(
                                text = "₹ 480",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpendWiseTheme.colors.textPrimary
                            )
                        }
                        Text(
                            text = "Category: Food & Dining • Today",
                            fontSize = 12.sp,
                            color = SpendWiseTheme.colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = "Add as Expense",
                    onClick = {
                        viewModel.addExpense(
                            amount = 480.0,
                            title = "Blue Tokai Coffee",
                            category = "Food & Dining",
                            date = "Today",
                            paymentMethod = "UPI",
                            tags = listOf("Coffee", "Receipt"),
                            notes = "Scanned receipt from Blue Tokai Coffee Roasters."
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "scan_receipt_save_button"
                )
            }
        }
    }
}
