package com.example.model

enum class TransactionType {
    EXPENSE, INCOME
}

data class TransactionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: String, // e.g. "12 Feb 2025" or "Today"
    val time: String = "02:30 PM",
    val paymentMethod: String = "HDFC Credit Card",
    val tags: List<String> = listOf("Essentials"),
    val notes: String = "",
    val iconType: String = "shopping",
    val colorHex: Long = 0xFFEF9C8D
)

data class CategoryAllocation(
    val categoryName: String,
    val percentage: Int,
    val amount: Double,
    val colorHex: Long = 0xFF9CC9FF,
    val iconType: String = "food"
)

data class BudgetItem(
    val id: String,
    val name: String,
    val month: String,
    val totalLimit: Double,
    val spent: Double,
    val allocations: List<CategoryAllocation> = emptyList(),
    val alertThresholdPercent: Int = 90
)

data class GoalContribution(
    val id: String,
    val amount: Double,
    val date: String,
    val note: String = "Added Money"
)

data class GoalItem(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentSavings: Double,
    val targetDate: String,
    val category: String,
    val iconType: String = "laptop",
    val colorHex: Long = 0xFF9CC9FF,
    val description: String = "",
    val contributions: List<GoalContribution> = emptyList()
) {
    val progress: Float
        get() = if (targetAmount > 0) ((currentSavings / targetAmount).toFloat()).coerceIn(0f, 1f) else 0f

    val progressPercent: Int
        get() = (progress * 100).toInt()

    val remainingAmount: Double
        get() = (targetAmount - currentSavings).coerceAtLeast(0.0)

    val isCompleted: Boolean
        get() = currentSavings >= targetAmount
}

data class CategoryItem(
    val id: String,
    val name: String,
    val iconType: String,
    val colorHex: Long,
    val spentAmount: Double,
    val transactionCount: Int
)

data class UserProfile(
    val name: String = "Ashish Singh",
    val email: String = "ashish.singh@example.com",
    val currency: String = "INR ₹",
    val monthlyIncome: Double = 45000.0,
    val monthlyBudget: Double = 30000.0,
    val preferredCategories: List<String> = listOf("Food & Dining", "Shopping", "Transport", "Bills & Utilities"),
    val memberSince: String = "12 Feb 2025",
    val accountType: String = "Free Plan"
)
