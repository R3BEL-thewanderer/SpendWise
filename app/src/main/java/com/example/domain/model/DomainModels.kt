package com.example.domain.model

enum class BudgetStatus {
    UNDER_BUDGET,
    NEAR_LIMIT,
    OVER_BUDGET
}

data class FinanceTotals(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double,
    val netCashFlow: Double,
    val savings: Double,
    val savingsRate: Double
)

data class CategorySpendingResult(
    val categoryName: String,
    val spentAmount: Double,
    val percentageOfTotal: Double,
    val transactionCount: Int
)

data class CategoryBudgetResult(
    val categoryName: String,
    val allocatedLimit: Double,
    val spent: Double,
    val remaining: Double,
    val usagePercentage: Double,
    val remainingPercentage: Double,
    val overBudgetAmount: Double,
    val status: BudgetStatus
)

data class BudgetCalculationResult(
    val budgetId: String,
    val name: String,
    val month: String,
    val totalLimit: Double,
    val totalSpent: Double,
    val remainingBudget: Double,
    val usagePercentage: Double,
    val remainingPercentage: Double,
    val overBudgetAmount: Double,
    val status: BudgetStatus,
    val categoryResults: List<CategoryBudgetResult> = emptyList()
)

data class GoalCalculationResult(
    val goalId: String,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val remainingAmount: Double,
    val progressPercent: Double,
    val isCompleted: Boolean,
    val gulakFillRatio: Float,
    val contributionCount: Int
)

enum class DatePeriod {
    ALL_TIME,
    CURRENT_WEEK,
    CURRENT_MONTH,
    PREVIOUS_MONTH,
    CUSTOM
}

data class CustomDateRange(
    val startDate: String,
    val endDate: String
)

data class MonthlyTrendItem(
    val monthKey: String,
    val monthLabel: String,
    val income: Double,
    val expenses: Double,
    val net: Double
)

data class AnalyticsSummary(
    val period: DatePeriod,
    val totals: FinanceTotals,
    val categoryBreakdown: List<CategorySpendingResult>,
    val budgetResults: List<BudgetCalculationResult>,
    val goalResults: List<GoalCalculationResult>,
    val monthlyTrends: List<MonthlyTrendItem>,
    val topCategory: CategorySpendingResult?
)

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val field: String, val message: String) : ValidationResult()

    val isValid: Boolean get() = this is Valid
}
