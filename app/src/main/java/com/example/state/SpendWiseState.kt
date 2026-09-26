package com.example.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.model.BudgetItem
import com.example.model.CategoryAllocation
import com.example.model.CategoryItem
import com.example.model.GoalContribution
import com.example.model.GoalItem
import com.example.model.TransactionItem
import com.example.model.TransactionType
import com.example.model.UserProfile

enum class AppScreen {
    WELCOME,
    SIGN_UP,
    SIGN_IN,
    FORGOT_PASSWORD,
    PROFILE_SETUP,
    HOME,
    TRANSACTIONS,
    TRANSACTION_DETAIL,
    ADD_EXPENSE,
    EDIT_EXPENSE,
    ADD_INCOME,
    EDIT_INCOME,
    BUDGETS,
    CREATE_BUDGET,
    BUDGET_DETAIL,
    EDIT_BUDGET,
    GOALS,
    CREATE_GOAL,
    GOAL_DETAIL,
    ADD_MONEY_GOAL,
    CATEGORIES,
    ADD_CATEGORY,
    EDIT_CATEGORY,
    ANALYTICS,
    PROFILE,
    SETTINGS,
    APPEARANCE
}

enum class ActiveModal {
    NONE,
    DELETE_TRANSACTION,
    DELETE_INCOME,
    DELETE_BUDGET,
    DELETE_GOAL,
    LOGOUT,
    TRANSACTION_FILTER,
    SCAN_RECEIPT,
    ERROR_STATE
}

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class TransactionFilter(
    val dateRange: String = "All Time",
    val selectedCategory: String = "All",
    val type: String = "All", // "All", "Expenses", "Income"
    val paymentMethod: String = "All",
    val minAmount: Double = 0.0,
    val maxAmount: Double = 100000.0
)

class SpendWiseViewModel : ViewModel() {

    // Navigation & Screen Stack
    var currentScreen by mutableStateOf(AppScreen.WELCOME)
        private set
    private val backStack = mutableListOf<AppScreen>()

    // Modal / Sheet Overlays
    var activeModal by mutableStateOf(ActiveModal.NONE)
    var toastMessage by mutableStateOf<String?>(null)

    // Theme Mode
    var themeMode by mutableStateOf(ThemeMode.LIGHT)

    // User Profile
    var userProfile by mutableStateOf(UserProfile())

    // Metrics (Mock state that can be adjusted dynamically)
    var totalBalance by mutableDoubleStateOf(48250.0)
    var monthlyIncome by mutableDoubleStateOf(45000.0)
    var monthlyExpenses by mutableDoubleStateOf(24580.0)
    var isBalanceVisible by mutableStateOf(true)

    // Selections
    var selectedTransaction by mutableStateOf<TransactionItem?>(null)
    var selectedGoal by mutableStateOf<GoalItem?>(null)
    var selectedBudget by mutableStateOf<BudgetItem?>(null)
    var selectedCategory by mutableStateOf<CategoryItem?>(null)

    // Filters & Search
    var transactionSearchQuery by mutableStateOf("")
    var transactionFilter by mutableStateOf(TransactionFilter())

    // Mock Transactions
    val transactions = mutableStateListOf<TransactionItem>(
        TransactionItem(
            id = "tx-1",
            title = "Starbucks",
            subtitle = "Food & Drinks",
            amount = 320.0,
            type = TransactionType.EXPENSE,
            category = "Food & Drinks",
            date = "Today",
            time = "10:24 AM",
            paymentMethod = "HDFC Credit Card",
            tags = listOf("Coffee", "Casual"),
            notes = "Morning iced latte and croissant with team.",
            iconType = "coffee",
            colorHex = 0xFFB9DEC9
        ),
        TransactionItem(
            id = "tx-2",
            title = "Blinkit",
            subtitle = "Groceries",
            amount = 420.0,
            type = TransactionType.EXPENSE,
            category = "Food & Dining",
            date = "Today",
            time = "10:24 AM",
            paymentMethod = "UPI",
            tags = listOf("Groceries", "Essentials"),
            notes = "Fresh vegetables, milk and bread.",
            iconType = "groceries",
            colorHex = 0xFFF5D98A
        ),
        TransactionItem(
            id = "tx-3",
            title = "Salary Credit",
            subtitle = "Income",
            amount = 45000.0,
            type = TransactionType.INCOME,
            category = "Salary",
            date = "Today",
            time = "07:00 AM",
            paymentMethod = "Bank Transfer",
            tags = listOf("Income", "Monthly"),
            notes = "Monthly salary deposit from Tech Corp.",
            iconType = "salary",
            colorHex = 0xFFB9DEC9
        ),
        TransactionItem(
            id = "tx-4",
            title = "Swiggy",
            subtitle = "Food & Dining",
            amount = 320.0,
            type = TransactionType.EXPENSE,
            category = "Food & Dining",
            date = "Today",
            time = "08:16 AM",
            paymentMethod = "UPI",
            tags = listOf("Food", "Breakfast"),
            notes = "South Indian breakfast combo.",
            iconType = "food",
            colorHex = 0xFFEF9C8D
        ),
        TransactionItem(
            id = "tx-5",
            title = "Uber",
            subtitle = "Transport",
            amount = 220.0,
            type = TransactionType.EXPENSE,
            category = "Transport",
            date = "Today",
            time = "12:14 AM",
            paymentMethod = "Credit Card",
            tags = listOf("Commute", "Cab"),
            notes = "Ride back home from office.",
            iconType = "transport",
            colorHex = 0xFF9CC9FF
        ),
        TransactionItem(
            id = "tx-6",
            title = "Amazon",
            subtitle = "Shopping",
            amount = 2499.0,
            type = TransactionType.EXPENSE,
            category = "Shopping",
            date = "Yesterday",
            time = "09:42 PM",
            paymentMethod = "HDFC Credit Card",
            tags = listOf("Electronics", "Essentials"),
            notes = "Noise cancelling wireless headphones for study.",
            iconType = "shopping",
            colorHex = 0xFFEF9C8D
        ),
        TransactionItem(
            id = "tx-7",
            title = "Zomato",
            subtitle = "Food & Dining",
            amount = 320.0,
            type = TransactionType.EXPENSE,
            category = "Food & Dining",
            date = "Yesterday",
            time = "02:18 PM",
            paymentMethod = "UPI",
            tags = listOf("Lunch"),
            notes = "Healthy salad bowl.",
            iconType = "food",
            colorHex = 0xFFEF9C8D
        ),
        TransactionItem(
            id = "tx-8",
            title = "Jio Recharge",
            subtitle = "Bills & Utilities",
            amount = 239.0,
            type = TransactionType.EXPENSE,
            category = "Bills & Utilities",
            date = "Yesterday",
            time = "11:05 AM",
            paymentMethod = "UPI",
            tags = listOf("Recharge", "Utility"),
            notes = "Monthly mobile data recharge.",
            iconType = "bills",
            colorHex = 0xFF9CC9FF
        )
    )

    // Mock Goals
    val goals = mutableStateListOf<GoalItem>(
        GoalItem(
            id = "goal-1",
            name = "New Laptop",
            targetAmount = 80000.0,
            currentSavings = 42000.0,
            targetDate = "30 Jun 2026",
            category = "Electronics",
            iconType = "laptop",
            colorHex = 0xFF9CC9FF,
            description = "High performance laptop for development and productivity.",
            contributions = listOf(
                GoalContribution("c-1", 5000.0, "12 Mar 2026", "Added Money"),
                GoalContribution("c-2", 10000.0, "28 Feb 2026", "Bonus savings"),
                GoalContribution("c-3", 7000.0, "14 Feb 2026", "Valentine savings")
            )
        ),
        GoalItem(
            id = "goal-2",
            name = "Goa Trip",
            targetAmount = 30000.0,
            currentSavings = 18500.0,
            targetDate = "15 Apr 2026",
            category = "Travel",
            iconType = "beach",
            colorHex = 0xFFF4C7B5,
            description = "Weekend beach getaway with college friends.",
            contributions = listOf(
                GoalContribution("c-4", 3500.0, "10 Mar 2026", "Flight ticket deposit"),
                GoalContribution("c-5", 5000.0, "25 Feb 2026", "Hotel split")
            )
        ),
        GoalItem(
            id = "goal-3",
            name = "Emergency Fund",
            targetAmount = 100000.0,
            currentSavings = 35000.0,
            targetDate = "31 Dec 2026",
            category = "Savings",
            iconType = "shield",
            colorHex = 0xFFF5D98A,
            description = "6 months safety buffer for peace of mind.",
            contributions = listOf(
                GoalContribution("c-6", 15000.0, "01 Mar 2026", "Quarterly allocation")
            )
        ),
        GoalItem(
            id = "goal-4",
            name = "New Bike",
            targetAmount = 200000.0,
            currentSavings = 40000.0,
            targetDate = "30 Nov 2026",
            category = "Vehicle",
            iconType = "bike",
            colorHex = 0xFFB9DEC9,
            description = "Royal Enfield cruiser for weekend road trips.",
            contributions = listOf(
                GoalContribution("c-7", 10000.0, "18 Feb 2026", "Initial downpayment pool")
            )
        )
    )

    // Mock Budgets
    val budgets = mutableStateListOf<BudgetItem>(
        BudgetItem(
            id = "budget-1",
            name = "March Budget",
            month = "March 2025",
            totalLimit = 30000.0,
            spent = 18420.0,
            alertThresholdPercent = 90,
            allocations = listOf(
                CategoryAllocation("Food & Drinks", 25, 7500.0, 0xFF9CC9FF, "food"),
                CategoryAllocation("Shopping", 20, 6000.0, 0xFFEF9C8D, "shopping"),
                CategoryAllocation("Transport", 15, 4500.0, 0xFFC9B8FF, "transport"),
                CategoryAllocation("Bills & Utilities", 20, 6000.0, 0xFFF5D98A, "bills"),
                CategoryAllocation("Entertainment", 10, 3000.0, 0xFFB9DEC9, "entertainment"),
                CategoryAllocation("Other", 10, 3000.0, 0xFFF4C7B5, "other")
            )
        )
    )

    // Mock Categories
    val categories = mutableStateListOf<CategoryItem>(
        CategoryItem("cat-1", "Food & Drinks", "food", 0xFF9CC9FF, 6240.0, 42),
        CategoryItem("cat-2", "Shopping", "shopping", 0xFFEF9C8D, 4890.0, 28),
        CategoryItem("cat-3", "Transport", "transport", 0xFFC9B8FF, 3210.0, 24),
        CategoryItem("cat-4", "Bills & Utilities", "bills", 0xFFF5D98A, 5600.0, 18),
        CategoryItem("cat-5", "Entertainment", "entertainment", 0xFFB9DEC9, 2480.0, 16),
        CategoryItem("cat-6", "Health", "health", 0xFFEF9C8D, 1920.0, 12),
        CategoryItem("cat-7", "Education", "education", 0xFF9CC9FF, 1200.0, 8),
        CategoryItem("cat-8", "Other", "other", 0xFFF4C7B5, 980.0, 9)
    )

    init {
        selectedGoal = goals.firstOrNull()
        selectedBudget = budgets.firstOrNull()
        selectedTransaction = transactions.firstOrNull()
        selectedCategory = categories.firstOrNull()
    }

    // Navigation Methods
    fun navigateTo(screen: AppScreen) {
        if (currentScreen != screen) {
            backStack.add(currentScreen)
            currentScreen = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    fun showToast(message: String) {
        toastMessage = message
    }

    fun dismissToast() {
        toastMessage = null
    }

    // Transaction Operations
    fun addExpense(
        amount: Double,
        title: String,
        category: String,
        date: String,
        paymentMethod: String,
        tags: List<String>,
        notes: String
    ) {
        val newTx = TransactionItem(
            id = "tx-${System.currentTimeMillis()}",
            title = if (title.isNotBlank()) title else category,
            subtitle = category,
            amount = amount,
            type = TransactionType.EXPENSE,
            category = category,
            date = if (date.isNotBlank()) date else "Today",
            time = "Now",
            paymentMethod = paymentMethod,
            tags = tags,
            notes = notes,
            iconType = when (category.lowercase()) {
                "shopping" -> "shopping"
                "transport" -> "transport"
                "bills & utilities" -> "bills"
                "entertainment" -> "entertainment"
                "health" -> "health"
                else -> "food"
            },
            colorHex = when (category.lowercase()) {
                "shopping" -> 0xFFEF9C8D
                "transport" -> 0xFF9CC9FF
                "bills & utilities" -> 0xFFF5D98A
                "entertainment" -> 0xFFB9DEC9
                else -> 0xFFC9B8FF
            }
        )
        transactions.add(0, newTx)
        monthlyExpenses += amount
        totalBalance -= amount
        showToast("Expense added successfully")
    }

    fun addIncome(
        amount: Double,
        source: String,
        date: String,
        category: String,
        notes: String
    ) {
        val newTx = TransactionItem(
            id = "tx-${System.currentTimeMillis()}",
            title = source,
            subtitle = if (category.isNotBlank()) category else "Income",
            amount = amount,
            type = TransactionType.INCOME,
            category = "Income",
            date = if (date.isNotBlank()) date else "Today",
            time = "Now",
            paymentMethod = "Direct Deposit",
            tags = listOf("Income"),
            notes = notes,
            iconType = "salary",
            colorHex = 0xFFB9DEC9
        )
        transactions.add(0, newTx)
        monthlyIncome += amount
        totalBalance += amount
        showToast("Income added successfully")
    }

    fun updateTransaction(updatedTx: TransactionItem) {
        val idx = transactions.indexOfFirst { it.id == updatedTx.id }
        if (idx != -1) {
            transactions[idx] = updatedTx
            selectedTransaction = updatedTx
            showToast("Changes saved")
        }
    }

    fun deleteTransaction(tx: TransactionItem) {
        transactions.removeIf { it.id == tx.id }
        if (tx.type == TransactionType.EXPENSE) {
            monthlyExpenses = (monthlyExpenses - tx.amount).coerceAtLeast(0.0)
            totalBalance += tx.amount
        } else {
            monthlyIncome = (monthlyIncome - tx.amount).coerceAtLeast(0.0)
            totalBalance -= tx.amount
        }
        if (selectedTransaction?.id == tx.id) {
            selectedTransaction = transactions.firstOrNull()
        }
        showToast("Transaction deleted")
    }

    // Goal Operations
    fun addGoal(
        name: String,
        targetAmount: Double,
        currentSavings: Double,
        targetDate: String,
        category: String,
        iconType: String,
        colorHex: Long,
        description: String
    ) {
        val newGoal = GoalItem(
            id = "goal-${System.currentTimeMillis()}",
            name = name,
            targetAmount = targetAmount,
            currentSavings = currentSavings,
            targetDate = targetDate,
            category = category,
            iconType = iconType,
            colorHex = colorHex,
            description = description,
            contributions = if (currentSavings > 0) listOf(GoalContribution("c-${System.currentTimeMillis()}", currentSavings, "Today", "Initial deposit")) else emptyList()
        )
        goals.add(0, newGoal)
        selectedGoal = newGoal
        showToast("Goal created successfully")
    }

    fun addMoneyToGoal(goalId: String, amount: Double) {
        val idx = goals.indexOfFirst { it.id == goalId }
        if (idx != -1) {
            val g = goals[idx]
            val newSavings = g.currentSavings + amount
            val newContribution = GoalContribution(
                id = "c-${System.currentTimeMillis()}",
                amount = amount,
                date = "Today",
                note = "Added Money"
            )
            val updated = g.copy(
                currentSavings = newSavings,
                contributions = listOf(newContribution) + g.contributions
            )
            goals[idx] = updated
            selectedGoal = updated
            showToast("₹${amount.toInt()} added to ${g.name}!")
        }
    }

    fun deleteGoal(goal: GoalItem) {
        goals.removeIf { it.id == goal.id }
        if (selectedGoal?.id == goal.id) {
            selectedGoal = goals.firstOrNull()
        }
        showToast("Goal deleted")
    }

    // Budget Operations
    fun createBudget(
        name: String,
        totalLimit: Double,
        month: String,
        allocations: List<CategoryAllocation>
    ) {
        val newBudget = BudgetItem(
            id = "budget-${System.currentTimeMillis()}",
            name = name,
            month = month,
            totalLimit = totalLimit,
            spent = 0.0,
            allocations = allocations
        )
        budgets.add(0, newBudget)
        selectedBudget = newBudget
        showToast("Budget created")
    }

    fun updateBudget(updated: BudgetItem) {
        val idx = budgets.indexOfFirst { it.id == updated.id }
        if (idx != -1) {
            budgets[idx] = updated
            selectedBudget = updated
            showToast("Budget updated")
        }
    }

    fun deleteBudget(budget: BudgetItem) {
        budgets.removeIf { it.id == budget.id }
        if (selectedBudget?.id == budget.id) {
            selectedBudget = budgets.firstOrNull()
        }
        showToast("Budget deleted")
    }

    // Category Operations
    fun addCategory(name: String, iconType: String, colorHex: Long) {
        val newCat = CategoryItem(
            id = "cat-${System.currentTimeMillis()}",
            name = name,
            iconType = iconType,
            colorHex = colorHex,
            spentAmount = 0.0,
            transactionCount = 0
        )
        categories.add(newCat)
        selectedCategory = newCat
        showToast("Category added")
    }

    fun updateCategory(updated: CategoryItem) {
        val idx = categories.indexOfFirst { it.id == updated.id }
        if (idx != -1) {
            categories[idx] = updated
            selectedCategory = updated
            showToast("Category updated")
        }
    }

    fun deleteCategory(cat: CategoryItem) {
        categories.removeIf { it.id == cat.id }
        if (selectedCategory?.id == cat.id) {
            selectedCategory = categories.firstOrNull()
        }
        showToast("Category deleted")
    }
}
