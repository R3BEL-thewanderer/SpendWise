package com.example.domain.calculator

import com.example.domain.model.AnalyticsSummary
import com.example.domain.model.CustomDateRange
import com.example.domain.model.DatePeriod
import com.example.domain.model.MonthlyTrendItem
import com.example.domain.utils.DatePeriodUtils
import com.example.model.BudgetItem
import com.example.model.CategoryItem
import com.example.model.GoalItem
import com.example.model.TransactionItem
import com.example.model.TransactionType
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object AnalyticsCalculator {

    private val monthLabelFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)
    private val monthKeyFormatter = DateTimeFormatter.ofPattern("yyyy-MM", Locale.ENGLISH)

    /**
     * Calculates monthly trends for the last [monthCount] months ending at [now].
     */
    fun calculateMonthlyTrends(
        transactions: List<TransactionItem>,
        now: LocalDate = LocalDate.now(),
        monthCount: Int = 6
    ): List<MonthlyTrendItem> {
        val currentYm = YearMonth.from(now)
        val months = (monthCount - 1 downTo 0).map { currentYm.minusMonths(it.toLong()) }

        return months.map { ym ->
            val monthTxs = transactions.filter { tx ->
                val date = DatePeriodUtils.parseDate(tx.date, now)
                date != null && date.year == ym.year && date.month == ym.month
            }

            val income = FinanceCalculator.roundMoney(
                monthTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            )
            val expenses = FinanceCalculator.roundMoney(
                monthTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            )
            val net = FinanceCalculator.roundMoney(income - expenses)

            MonthlyTrendItem(
                monthKey = ym.format(monthKeyFormatter),
                monthLabel = ym.format(monthLabelFormatter),
                income = income,
                expenses = expenses,
                net = net
            )
        }
    }

    /**
     * Computes complete analytics summary for the selected period.
     */
    fun calculateAnalytics(
        transactions: List<TransactionItem>,
        categories: List<CategoryItem>,
        budgets: List<BudgetItem>,
        goals: List<GoalItem>,
        period: DatePeriod = DatePeriod.CURRENT_MONTH,
        now: LocalDate = LocalDate.now(),
        customRange: CustomDateRange? = null
    ): AnalyticsSummary {
        // Filter transactions for the period
        val periodTransactions = transactions.filter { tx ->
            DatePeriodUtils.isDateInPeriod(tx.date, period, now, customRange)
        }

        val totals = FinanceCalculator.calculateTotals(periodTransactions)
        val categoryBreakdown = CategoryCalculator.calculateCategoryBreakdown(categories, periodTransactions)
        val budgetResults = budgets.map { BudgetCalculator.calculateBudget(it, transactions, now) }
        val goalResults = GoalCalculator.calculateAllGoals(goals)
        val monthlyTrends = calculateMonthlyTrends(transactions, now, 6)
        val topCategory = categoryBreakdown.firstOrNull { it.spentAmount > 0.0 }

        return AnalyticsSummary(
            period = period,
            totals = totals,
            categoryBreakdown = categoryBreakdown,
            budgetResults = budgetResults,
            goalResults = goalResults,
            monthlyTrends = monthlyTrends,
            topCategory = topCategory
        )
    }
}
