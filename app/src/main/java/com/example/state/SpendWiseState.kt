package com.example.state

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.LocalStorageManager
import com.example.data.local.SettingsData
import com.example.domain.ai.NaturalLanguageAssistant
import com.example.domain.ai.SmartInsightsEngine
import com.example.domain.ai.model.AssistantMessage
import com.example.domain.ai.model.MonthlyAiSummary
import com.example.domain.ai.model.SmartInsight
import com.example.domain.calculator.AnalyticsCalculator
import com.example.domain.calculator.BudgetCalculator
import com.example.domain.calculator.CategoryCalculator
import com.example.domain.calculator.FinanceCalculator
import com.example.domain.calculator.GoalCalculator
import com.example.domain.filter.TransactionFilterService
import com.example.domain.model.AnalyticsSummary
import com.example.domain.model.BudgetCalculationResult
import com.example.domain.model.CategoryBudgetResult
import com.example.domain.model.CategorySpendingResult
import com.example.domain.model.CustomDateRange
import com.example.domain.model.DatePeriod
import com.example.domain.model.FinanceTotals
import com.example.domain.model.GoalCalculationResult
import com.example.domain.model.ValidationResult
import com.example.domain.validation.FinancialValidator
import com.example.model.BudgetItem
import com.example.model.CategoryAllocation
import com.example.model.CategoryItem
import com.example.model.GoalContribution
import com.example.model.GoalItem
import com.example.model.TransactionItem
import com.example.model.TransactionType
import com.example.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    DELETE_ACCOUNT,
    LOGOUT,
    TRANSACTION_FILTER,
    SCAN_RECEIPT,
    AI_ASSISTANT,
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

class SpendWiseViewModel(
    val storageManager: LocalStorageManager? = null
) : ViewModel() {

    var syncPersistenceForTesting: Boolean = false

    // Navigation & Screen Stack
    var currentScreen by mutableStateOf(AppScreen.WELCOME)
        private set
    private val backStack = mutableListOf<AppScreen>()

    // Modal / Sheet Overlays
    var activeModal by mutableStateOf(ActiveModal.NONE)
    var toastMessage by mutableStateOf<String?>(null)

    // Theme Mode
    private var _themeMode by mutableStateOf(ThemeMode.LIGHT)
    var themeMode: ThemeMode
        get() = _themeMode
        set(value) {
            _themeMode = value
            persistSettings()
        }

    // User Profile
    private var _userProfile by mutableStateOf(UserProfile())
    var userProfile: UserProfile
        get() = _userProfile
        set(value) {
            _userProfile = value
            persistUserProfile()
        }

    // Metrics (Persisted State)
    var totalBalance by mutableDoubleStateOf(48250.0)
    var monthlyIncome by mutableDoubleStateOf(45000.0)
    var monthlyExpenses by mutableDoubleStateOf(24580.0)

    private var _isBalanceVisible by mutableStateOf(true)
    var isBalanceVisible: Boolean
        get() = _isBalanceVisible
        set(value) {
            _isBalanceVisible = value
            persistSettings()
        }

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
        if (storageManager != null) {
            if (!storageManager.isInitialized()) {
                // First launch: recalculate metrics from demo data and initialize storage
                recalculateMetrics()
                storageManager.initializeDemoData(
                    demoUser = _userProfile,
                    demoTransactions = transactions.toList(),
                    demoCategories = categories.toList(),
                    demoBudgets = budgets.toList(),
                    demoGoals = goals.toList(),
                    demoSettings = currentSettings()
                )
            } else {
                // Subsequent launch: restore persisted data from local storage
                val data = storageManager.loadAll()
                _userProfile = data.userProfile
                transactions.clear()
                transactions.addAll(data.transactions)
                categories.clear()
                categories.addAll(data.categories)
                budgets.clear()
                budgets.addAll(data.budgets)
                goals.clear()
                goals.addAll(data.goals)

                _themeMode = try {
                    ThemeMode.valueOf(data.settings.themeMode)
                } catch (_: Exception) {
                    ThemeMode.LIGHT
                }
                _isBalanceVisible = data.settings.isBalanceVisible
                recalculateMetrics()
            }
        } else {
            recalculateMetrics()
        }
        selectedGoal = goals.firstOrNull()
        selectedBudget = budgets.firstOrNull()
        selectedTransaction = transactions.firstOrNull()
        selectedCategory = categories.firstOrNull()
    }

    /**
     * Recalculates all derived metrics using the central domain calculators.
     * Guarantees data consistency across the entire application.
     */
    fun recalculateMetrics() {
        val totals = FinanceCalculator.calculateTotals(transactions)
        monthlyIncome = totals.totalIncome
        monthlyExpenses = totals.totalExpenses
        totalBalance = totals.balance

        // Update category spent amounts and transaction counts
        for (i in categories.indices) {
            val cat = categories[i]
            val catTxs = transactions.filter {
                it.type == TransactionType.EXPENSE && it.category.equals(cat.name, ignoreCase = true)
            }
            val spent = FinanceCalculator.roundMoney(catTxs.sumOf { it.amount })
            if (cat.spentAmount != spent || cat.transactionCount != catTxs.size) {
                categories[i] = cat.copy(spentAmount = spent, transactionCount = catTxs.size)
            }
        }

        // Update budget spent amounts based on transactions for each budget's month
        for (i in budgets.indices) {
            val b = budgets[i]
            val result = BudgetCalculator.calculateBudget(b, transactions)
            if (b.spent != result.totalSpent) {
                budgets[i] = b.copy(spent = result.totalSpent)
            }
        }
    }

    // Domain Accessors for UI
    fun getFinanceTotals(): FinanceTotals = FinanceCalculator.calculateTotals(transactions)

    fun getPeriodTotals(period: DatePeriod, customRange: CustomDateRange? = null): FinanceTotals =
        FinanceCalculator.calculatePeriodTotals(transactions, period, customRange = customRange)

    fun getBudgetResult(budget: BudgetItem): BudgetCalculationResult =
        BudgetCalculator.calculateBudget(budget, transactions)

    fun getCategoryBudgetResult(allocation: CategoryAllocation, budgetMonth: String): CategoryBudgetResult =
        BudgetCalculator.calculateCategoryBudget(allocation, transactions, budgetMonth)

    fun getGoalResult(goal: GoalItem): GoalCalculationResult =
        GoalCalculator.calculateGoal(goal)

    fun getCategoryBreakdown(): List<CategorySpendingResult> =
        CategoryCalculator.calculateCategoryBreakdown(categories, transactions)

    fun getAnalytics(period: DatePeriod = DatePeriod.CURRENT_MONTH): AnalyticsSummary =
        AnalyticsCalculator.calculateAnalytics(transactions, categories, budgets, goals, period)

    fun filterTransactions(
        typeFilter: String = "All",
        categoryFilter: String = "All",
        datePeriod: DatePeriod = DatePeriod.ALL_TIME,
        searchQuery: String = transactionSearchQuery
    ): List<TransactionItem> = TransactionFilterService.filter(
        transactions = transactions,
        typeFilter = typeFilter,
        categoryFilter = categoryFilter,
        datePeriod = datePeriod,
        searchQuery = searchQuery,
        paymentMethod = transactionFilter.paymentMethod,
        minAmount = transactionFilter.minAmount,
        maxAmount = transactionFilter.maxAmount
    )

    // AI & Smart Insights Layer
    val assistantMessages = mutableStateListOf<AssistantMessage>()
    var isAssistantThinking by mutableStateOf(false)

    fun getSmartInsights(): List<SmartInsight> {
        return SmartInsightsEngine.generateInsights(
            totals = getFinanceTotals(),
            categoryBreakdown = getCategoryBreakdown(),
            budgetResults = budgets.map { getBudgetResult(it) },
            goalResults = goals.map { getGoalResult(it) },
            monthlyTrends = AnalyticsCalculator.calculateMonthlyTrends(transactions),
            transactions = transactions.toList()
        )
    }

    fun getMonthlyAiSummary(monthLabel: String = "This Month"): MonthlyAiSummary {
        return SmartInsightsEngine.generateMonthlySummary(
            monthLabel = monthLabel,
            totals = getFinanceTotals(),
            topCategory = getCategoryBreakdown().firstOrNull { it.spentAmount > 0.0 },
            budgetResults = budgets.map { getBudgetResult(it) }
        )
    }

    fun openAiAssistant() {
        if (assistantMessages.isEmpty()) {
            val name = userProfile.name.split(" ").firstOrNull() ?: "there"
            assistantMessages.add(
                AssistantMessage(
                    id = "msg-initial",
                    text = "Hello $name! I'm your SpendWise AI assistant. I analyze your verified financial data to provide instant clarity.\n\nWhat would you like to know today?",
                    isUser = false,
                    suggestedActions = listOf(
                        "Where did most of my money go?",
                        "How much on food?",
                        "Am I spending more than last month?",
                        "How is my budget?",
                        "What is my balance?"
                    )
                )
            )
        }
        activeModal = ActiveModal.AI_ASSISTANT
    }

    fun askAssistant(userQuery: String) {
        val trimmed = userQuery.trim()
        if (trimmed.isBlank()) return
        val userMsg = AssistantMessage(
            id = "msg-${System.currentTimeMillis()}-u",
            text = trimmed,
            isUser = true
        )
        assistantMessages.add(userMsg)
        isAssistantThinking = true

        viewModelScope.launch {
            val answer = NaturalLanguageAssistant.answerQuery(
                query = trimmed,
                totals = getFinanceTotals(),
                categoryBreakdown = getCategoryBreakdown(),
                budgetResults = budgets.map { getBudgetResult(it) },
                goalResults = goals.map { getGoalResult(it) },
                monthlyTrends = AnalyticsCalculator.calculateMonthlyTrends(transactions),
                transactions = transactions.toList()
            )
            val assistantMsg = AssistantMessage(
                id = "msg-${System.currentTimeMillis()}-a",
                text = answer,
                isUser = false,
                suggestedActions = listOf(
                    "Where did most of my money go?",
                    "How much on food?",
                    "Am I spending more than last month?",
                    "How is my budget?"
                )
            )
            assistantMessages.add(assistantMsg)
            isAssistantThinking = false
        }
    }

    private fun currentSettings(): SettingsData {
        return SettingsData(
            themeMode = _themeMode.name,
            isBalanceVisible = _isBalanceVisible,
            totalBalance = totalBalance,
            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses
        )
    }

    private fun persistTransactions() {
        val currentTxs = transactions.toList()
        val currentSets = currentSettings()
        val sm = storageManager ?: return
        if (syncPersistenceForTesting) {
            sm.saveTransactions(currentTxs)
            sm.saveSettings(currentSets)
        } else {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    sm.saveTransactions(currentTxs)
                    sm.saveSettings(currentSets)
                }
            } catch (_: Throwable) {
                sm.saveTransactions(currentTxs)
                sm.saveSettings(currentSets)
            }
        }
    }

    private fun persistGoals() {
        val currentGoals = goals.toList()
        val sm = storageManager ?: return
        if (syncPersistenceForTesting) {
            sm.saveGoals(currentGoals)
        } else {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    sm.saveGoals(currentGoals)
                }
            } catch (_: Throwable) {
                sm.saveGoals(currentGoals)
            }
        }
    }

    private fun persistBudgets() {
        val currentBudgets = budgets.toList()
        val sm = storageManager ?: return
        if (syncPersistenceForTesting) {
            sm.saveBudgets(currentBudgets)
        } else {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    sm.saveBudgets(currentBudgets)
                }
            } catch (_: Throwable) {
                sm.saveBudgets(currentBudgets)
            }
        }
    }

    private fun persistCategories() {
        val currentCategories = categories.toList()
        val sm = storageManager ?: return
        if (syncPersistenceForTesting) {
            sm.saveCategories(currentCategories)
        } else {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    sm.saveCategories(currentCategories)
                }
            } catch (_: Throwable) {
                sm.saveCategories(currentCategories)
            }
        }
    }

    private fun persistUserProfile() {
        val currentProfile = _userProfile
        val sm = storageManager ?: return
        if (syncPersistenceForTesting) {
            sm.saveUserProfile(currentProfile)
        } else {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    sm.saveUserProfile(currentProfile)
                }
            } catch (_: Throwable) {
                sm.saveUserProfile(currentProfile)
            }
        }
    }

    private fun persistSettings() {
        val currentSets = currentSettings()
        val sm = storageManager ?: return
        if (syncPersistenceForTesting) {
            sm.saveSettings(currentSets)
        } else {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    sm.saveSettings(currentSets)
                }
            } catch (_: Throwable) {
                sm.saveSettings(currentSets)
            }
        }
    }

    fun persistAllSync() {
        storageManager?.let { sm ->
            sm.saveUserProfile(_userProfile)
            sm.saveTransactions(transactions.toList())
            sm.saveCategories(categories.toList())
            sm.saveBudgets(budgets.toList())
            sm.saveGoals(goals.toList())
            sm.saveSettings(currentSettings())
        }
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
    ): Boolean {
        val txTitle = if (title.isNotBlank()) title else category
        val validation = FinancialValidator.validateTransaction(
            amount = amount,
            type = TransactionType.EXPENSE,
            category = category,
            title = txTitle,
            date = if (date.isNotBlank()) date else "Today"
        )
        if (!validation.isValid && validation is ValidationResult.Invalid) {
            showToast(validation.message)
            return false
        }

        val roundedAmount = FinanceCalculator.roundMoney(amount)
        val newTx = TransactionItem(
            id = "tx-${System.currentTimeMillis()}",
            title = txTitle,
            subtitle = category,
            amount = roundedAmount,
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
        recalculateMetrics()
        showToast("Expense added successfully")
        persistTransactions()
        return true
    }

    fun addIncome(
        amount: Double,
        source: String,
        date: String,
        category: String,
        notes: String
    ): Boolean {
        val validation = FinancialValidator.validateTransaction(
            amount = amount,
            type = TransactionType.INCOME,
            category = if (category.isNotBlank()) category else "Income",
            title = source,
            date = if (date.isNotBlank()) date else "Today"
        )
        if (!validation.isValid && validation is ValidationResult.Invalid) {
            showToast(validation.message)
            return false
        }

        val roundedAmount = FinanceCalculator.roundMoney(amount)
        val newTx = TransactionItem(
            id = "tx-${System.currentTimeMillis()}",
            title = source,
            subtitle = if (category.isNotBlank()) category else "Income",
            amount = roundedAmount,
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
        recalculateMetrics()
        showToast("Income added successfully")
        persistTransactions()
        return true
    }

    fun updateTransaction(updatedTx: TransactionItem): Boolean {
        val validation = FinancialValidator.validateTransaction(
            amount = updatedTx.amount,
            type = updatedTx.type,
            category = updatedTx.category,
            title = updatedTx.title,
            date = updatedTx.date
        )
        if (!validation.isValid && validation is ValidationResult.Invalid) {
            showToast(validation.message)
            return false
        }

        val idx = transactions.indexOfFirst { it.id == updatedTx.id }
        if (idx != -1) {
            val rounded = updatedTx.copy(amount = FinanceCalculator.roundMoney(updatedTx.amount))
            transactions[idx] = rounded
            selectedTransaction = rounded
            recalculateMetrics()
            showToast("Changes saved")
            persistTransactions()
            return true
        }
        return false
    }

    fun deleteTransaction(tx: TransactionItem) {
        transactions.removeIf { it.id == tx.id }
        recalculateMetrics()
        if (selectedTransaction?.id == tx.id) {
            selectedTransaction = transactions.firstOrNull()
        }
        showToast("Transaction deleted")
        persistTransactions()
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
    ): Boolean {
        val validation = FinancialValidator.validateGoal(name, targetAmount, currentSavings)
        if (!validation.isValid && validation is ValidationResult.Invalid) {
            showToast(validation.message)
            return false
        }

        val roundedTarget = FinanceCalculator.roundMoney(targetAmount)
        val roundedSavings = FinanceCalculator.roundMoney(currentSavings)
        val newGoal = GoalItem(
            id = "goal-${System.currentTimeMillis()}",
            name = name,
            targetAmount = roundedTarget,
            currentSavings = roundedSavings,
            targetDate = targetDate,
            category = category,
            iconType = iconType,
            colorHex = colorHex,
            description = description,
            contributions = if (roundedSavings > 0) listOf(GoalContribution("c-${System.currentTimeMillis()}", roundedSavings, "Today", "Initial deposit")) else emptyList()
        )
        goals.add(0, newGoal)
        selectedGoal = newGoal
        showToast("Goal created successfully")
        persistGoals()
        return true
    }

    fun addMoneyToGoal(goalId: String, amount: Double): Boolean {
        val goalExists = goals.any { it.id == goalId }
        val validation = FinancialValidator.validateContribution(amount, goalExists)
        if (!validation.isValid && validation is ValidationResult.Invalid) {
            showToast(validation.message)
            return false
        }

        val idx = goals.indexOfFirst { it.id == goalId }
        if (idx != -1) {
            val g = goals[idx]
            val roundedAmount = FinanceCalculator.roundMoney(amount)
            val newContribution = GoalContribution(
                id = "c-${System.currentTimeMillis()}",
                amount = roundedAmount,
                date = "Today",
                note = "Added Money"
            )
            val newContributions = listOf(newContribution) + g.contributions
            val newSavings = FinanceCalculator.roundMoney(g.currentSavings + roundedAmount)
            val updated = g.copy(
                currentSavings = newSavings,
                contributions = newContributions
            )
            goals[idx] = updated
            selectedGoal = updated
            showToast("₹${roundedAmount.toInt()} added to ${g.name}!")
            persistGoals()
            return true
        }
        return false
    }

    fun deleteGoal(goal: GoalItem) {
        goals.removeIf { it.id == goal.id }
        if (selectedGoal?.id == goal.id) {
            selectedGoal = goals.firstOrNull()
        }
        showToast("Goal deleted")
        persistGoals()
    }

    // Budget Operations
    fun createBudget(
        name: String,
        totalLimit: Double,
        month: String,
        allocations: List<CategoryAllocation>
    ): Boolean {
        val validation = FinancialValidator.validateBudget(name, totalLimit, month)
        if (!validation.isValid && validation is ValidationResult.Invalid) {
            showToast(validation.message)
            return false
        }

        val roundedLimit = FinanceCalculator.roundMoney(totalLimit)
        val newBudget = BudgetItem(
            id = "budget-${System.currentTimeMillis()}",
            name = name,
            month = month,
            totalLimit = roundedLimit,
            spent = 0.0,
            allocations = allocations
        )
        budgets.add(0, newBudget)
        selectedBudget = newBudget
        recalculateMetrics()
        showToast("Budget created")
        persistBudgets()
        return true
    }

    fun updateBudget(updated: BudgetItem) {
        val idx = budgets.indexOfFirst { it.id == updated.id }
        if (idx != -1) {
            val rounded = updated.copy(totalLimit = FinanceCalculator.roundMoney(updated.totalLimit))
            budgets[idx] = rounded
            selectedBudget = rounded
            recalculateMetrics()
            showToast("Budget updated")
            persistBudgets()
        }
    }

    fun deleteBudget(budget: BudgetItem) {
        budgets.removeIf { it.id == budget.id }
        if (selectedBudget?.id == budget.id) {
            selectedBudget = budgets.firstOrNull()
        }
        showToast("Budget deleted")
        persistBudgets()
    }

    // Category Operations
    fun addCategory(name: String, iconType: String, colorHex: Long): Boolean {
        val validation = FinancialValidator.validateCategory(name)
        if (!validation.isValid && validation is ValidationResult.Invalid) {
            showToast(validation.message)
            return false
        }

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
        recalculateMetrics()
        showToast("Category added")
        persistCategories()
        return true
    }

    fun updateCategory(updated: CategoryItem) {
        val idx = categories.indexOfFirst { it.id == updated.id }
        if (idx != -1) {
            categories[idx] = updated
            selectedCategory = updated
            recalculateMetrics()
            showToast("Category updated")
            persistCategories()
        }
    }

    fun deleteCategory(cat: CategoryItem) {
        categories.removeIf { it.id == cat.id }
        if (selectedCategory?.id == cat.id) {
            selectedCategory = categories.firstOrNull()
        }
        showToast("Category deleted")
        persistCategories()
    }

    fun deleteAccount() {
        val sm = storageManager
        if (sm != null) {
            if (syncPersistenceForTesting) {
                sm.clearAll()
            } else {
                try {
                    viewModelScope.launch(Dispatchers.IO) {
                        sm.clearAll()
                    }
                } catch (_: Throwable) {
                    sm.clearAll()
                }
            }
        }
        transactions.clear()
        goals.clear()
        budgets.clear()
        categories.clear()
        recalculateMetrics()
        currentScreen = AppScreen.WELCOME
        showToast("Account deleted and local data cleared")
    }
}

class SpendWiseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SpendWiseViewModel(LocalStorageManager(context)) as T
    }
}
