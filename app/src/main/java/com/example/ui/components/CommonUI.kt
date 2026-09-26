package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionItem
import com.example.model.TransactionType
import com.example.ui.theme.SpendWiseTheme
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    formatter.maximumFractionDigits = 0
    return "₹" + formatter.format(amount)
}

fun getIconForType(iconType: String): ImageVector {
    return when (iconType.lowercase()) {
        "coffee" -> Icons.Default.LocalCafe
        "food", "food & dining", "groceries" -> Icons.Default.Fastfood
        "shopping" -> Icons.Default.ShoppingBag
        "transport", "car", "commute" -> Icons.Default.DirectionsCar
        "bills", "bills & utilities" -> Icons.Default.Receipt
        "salary", "income", "money" -> Icons.Default.AttachMoney
        "laptop", "electronics" -> Icons.Default.Laptop
        "beach", "travel" -> Icons.Default.CardTravel
        "shield", "savings" -> Icons.Default.Security
        "bike" -> Icons.Default.TwoWheeler
        "gaming", "entertainment" -> Icons.Default.SportsEsports
        "health" -> Icons.Default.Favorite
        "education", "school" -> Icons.Default.School
        "bank" -> Icons.Default.AccountBalance
        else -> Icons.Default.Category
    }
}

@Composable
fun CategoryChip(
    name: String,
    icon: ImageVector? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color? = null
) {
    val isDark = SpendWiseTheme.colors.isDark
    val baseColor = color ?: SpendWiseTheme.colors.lavender

    val bg = if (isSelected) {
        baseColor.copy(alpha = if (isDark) 0.35f else 0.25f)
    } else {
        if (isDark) Color(0x1AFFFFFF) else Color.White.copy(alpha = 0.7f)
    }

    val border = if (isSelected) {
        BorderStroke(1.5.dp, baseColor)
    } else {
        BorderStroke(1.dp, if (isDark) Color(0x22FFFFFF) else Color(0x1F000000))
    }

    val textColor = if (isSelected) {
        if (isDark) Color.White else Color(0xFF171717)
    } else {
        SpendWiseTheme.colors.textSecondary
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = bg,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp),
                    tint = if (isSelected) baseColor else textColor
                )
            }
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun LargeAmountInput(
    amountText: String,
    onAmountChange: (String) -> Unit,
    currencySymbol: String = "₹",
    modifier: Modifier = Modifier,
    label: String = "Enter amount"
) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textMuted = SpendWiseTheme.colors.textMuted

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$currencySymbol ",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            BasicTextField(
                value = amountText,
                onValueChange = { newVal ->
                    // keep only digits
                    val filtered = newVal.filter { it.isDigit() }
                    if (filtered.length <= 9) {
                        onAmountChange(filtered)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Start
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(textPrimary),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (amountText.isEmpty()) {
                            Text(
                                text = "0",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMuted
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = textMuted
        )
    }
}

@Composable
fun TransactionRow(
    transaction: TransactionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val iconVector = getIconForType(transaction.iconType)
    val iconBgColor = Color(transaction.colorHex)
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tx_row_${transaction.id}"),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon in pastel rounded container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBgColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = transaction.category,
                    tint = iconBgColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${transaction.subtitle} • ${transaction.date}",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isExpense) "- " else "+ ") + formatCurrency(transaction.amount),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) textPrimary else Color(0xFF34C759)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.time,
                    fontSize = 11.sp,
                    color = SpendWiseTheme.colors.textMuted
                )
            }
        }
    }
}
