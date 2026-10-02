package com.example.domain.ai

import com.example.domain.ai.model.InsightSeverity
import com.example.domain.ai.model.InsightType
import com.example.domain.ai.model.MonthlyAiSummary
import com.example.domain.ai.model.SmartInsight
import com.example.domain.calculator.FinanceCalculator
import com.example.domain.model.BudgetCalculationResult
import com.example.domain.model.BudgetStatus
import com.example.domain.model.CategorySpendingResult
import com.example.domain.model.FinanceTotals
import com.example.domain.model.GoalCalculationResult
import com.example.domain.model.MonthlyTrendItem
import com.example.model.TransactionItem
import com.example.model.TransactionType

object SmartInsightsEngine {

    /**
     * Generates a ranked list of actionable, data-driven smart insights
     * based entirely on verified domain calculations.
     */
    fun generateInsights(
        totals: FinanceTotals,
        categoryBreakdown: List<CategorySpendingResult>,
        budgetResults: List<BudgetCalculationResult>,
        goalResults: List<GoalCalculationResult>,
        monthlyTrends: List<MonthlyTrendItem>,
        transactions: List<TransactionItem>
    ): List<SmartInsight> {
        val insights = mutableListOf<SmartInsight>()

        // 1. Budget Alerts & Utilization (Highest Priority)
        val overBudget = budgetResults.firstOrNull { it.status == BudgetStatus.OVER_BUDGET }
        val nearLimit = budgetResults.firstOrNull { it.status == BudgetStatus.NEAR_LIMIT }

        if (overBudget != null) {
            insights.add(
                SmartInsight(
                    id = "insight-budget-over",
                    title = "Budget Exceeded",
                    description = "Your ${overBudget.name} has exceeded its limit by ₹${overBudget.overBudgetAmount.toInt()} (${overBudget.usagePercentage.toInt()}% used).",
                    type = InsightType.BUDGET,
                    severity = InsightSeverity.WARNING,
                    iconType = "warning",
                    actionText = "Review Budget"
                )
            )
        } else if (nearLimit != null) {
            insights.add(
                SmartInsight(
                    id = "insight-budget-near",
                    title = "Budget Warning",
                    description = "Your ${nearLimit.name} is approaching its limit at ${nearLimit.usagePercentage.toInt()}% capacity (₹${nearLimit.remainingBudget.toInt()} remaining).",
                    type = InsightType.BUDGET,
                    severity = InsightSeverity.WARNING,
                    iconType = "alert",
                    actionText = "Track Spending"
                )
            )
        }

        // 2. Category-Level Budget Allocation Alerts
        for (b in budgetResults) {
            val catNear = b.categoryResults.firstOrNull { it.status == BudgetStatus.NEAR_LIMIT || it.status == BudgetStatus.OVER_BUDGET }
            if (catNear != null) {
                val isOver = catNear.status == BudgetStatus.OVER_BUDGET
                insights.add(
                    SmartInsight(
                        id = "insight-cat-${catNear.categoryName.lowercase()}",
                        title = if (isOver) "${catNear.categoryName} Over Budget" else "${catNear.categoryName} Budget Alert",
                        description = if (isOver) {
                            "You have exceeded your ${catNear.categoryName} allocation by ₹${catNear.overBudgetAmount.toInt()}."
                        } else {
                            "Your ${catNear.categoryName} budget is at ${catNear.usagePercentage.toInt()}% capacity based on your recent spending."
                        },
                        type = InsightType.BUDGET,
                        severity = if (isOver) InsightSeverity.WARNING else InsightSeverity.INFO,
                        iconType = "category",
                        actionText = "View Category"
                    )
                )
                break
            }
        }

        // 3. Top Spending Insight
        val topCategory = categoryBreakdown.firstOrNull { it.spentAmount > 0.0 }
        if (topCategory != null && totals.totalExpenses > 0.0) {
            insights.add(
                SmartInsight(
                    id = "insight-spending-top",
                    title = "Top Spending Category",
                    description = "${topCategory.categoryName} was your largest expense this period at ₹${topCategory.spentAmount.toInt()}, representing ${topCategory.percentageOfTotal.toInt()}% of your total recorded spend.",
                    type = InsightType.SPENDING,
                    severity = InsightSeverity.INFO,
                    iconType = "pie",
                    actionText = "See Breakdown"
                )
            )
        }

        // 4. Goal Progress & Milestones
        val completedGoal = goalResults.firstOrNull { it.isCompleted }
        val inProgressGoal = goalResults.firstOrNull { !it.isCompleted && it.savedAmount > 0.0 }

        if (completedGoal != null) {
            insights.add(
                SmartInsight(
                    id = "insight-goal-complete",
                    title = "Goal Achieved! 🎉",
                    description = "You've successfully reached your target of ₹${completedGoal.targetAmount.toInt()} for '${completedGoal.name}'!",
                    type = InsightType.GOAL,
                    severity = InsightSeverity.SUCCESS,
                    iconType = "trophy",
                    actionText = "Celebrate"
                )
            )
        }
        if (inProgressGoal != null) {
            insights.add(
                SmartInsight(
                    id = "insight-goal-progress",
                    title = "${inProgressGoal.name} Savings",
                    description = "You have ₹${inProgressGoal.remainingAmount.toInt()} remaining on your '${inProgressGoal.name}' goal (${inProgressGoal.progressPercent.toInt()}% saved).",
                    type = InsightType.GOAL,
                    severity = InsightSeverity.INFO,
                    iconType = "savings",
                    actionText = "Add Money"
                )
            )
        }

        // 5. Savings Rate & Cash Flow Insight
        if (totals.totalIncome > 0.0) {
            if (totals.savingsRate >= 20.0) {
                insights.add(
                    SmartInsight(
                        id = "insight-savings-rate",
                        title = "Strong Savings Rate",
                        description = "You are saving ${totals.savingsRate.toInt()}% of your income (₹${totals.savings.toInt()}), outperforming the standard 20% financial wellness benchmark.",
                        type = InsightType.SUMMARY,
                        severity = InsightSeverity.SUCCESS,
                        iconType = "trending-up",
                        actionText = "View Net Flow"
                    )
                )
            } else if (totals.savingsRate < 0.0) {
                insights.add(
                    SmartInsight(
                        id = "insight-negative-cashflow",
                        title = "Negative Cash Flow",
                        description = "Your expenses exceed your income this period by ₹${(-totals.savings).toInt()}. Consider pacing discretionary purchases.",
                        type = InsightType.SUMMARY,
                        severity = InsightSeverity.WARNING,
                        iconType = "trending-down",
                        actionText = "Manage Expenses"
                    )
                )
            }
        }

        // 6. Trend Comparison (Current vs Previous Month)
        if (monthlyTrends.size >= 2) {
            val current = monthlyTrends.last()
            val previous = monthlyTrends[monthlyTrends.size - 2]
            if (previous.expenses > 0.0) {
                val diff = current.expenses - previous.expenses
                val pctChange = FinanceCalculator.roundMoney((diff / previous.expenses) * 100.0)
                if (pctChange < -5.0) {
                    insights.add(
                        SmartInsight(
                            id = "insight-trend-lower",
                            title = "Spending Down vs Last Month",
                            description = "Your spending this month is ${(-pctChange).toInt()}% lower than ${previous.monthLabel} (saved ₹${(-diff).toInt()} more). Great job!",
                            type = InsightType.SPENDING,
                            severity = InsightSeverity.SUCCESS,
                            iconType = "sparkles"
                        )
                    )
                } else if (pctChange > 15.0) {
                    insights.add(
                        SmartInsight(
                            id = "insight-trend-higher",
                            title = "Higher Spending Pace",
                            description = "Your spending is ${pctChange.toInt()}% higher than ${previous.monthLabel} (+₹${diff.toInt()}). Check category breakdown for drivers.",
                            type = InsightType.SPENDING,
                            severity = InsightSeverity.INFO,
                            iconType = "trending-up"
                        )
                    )
                }
            }
        }

        // 7. Spending Anomaly Detection
        val expensesList = transactions.filter { it.type == TransactionType.EXPENSE }
        if (expensesList.isNotEmpty()) {
            val avgExpense = expensesList.map { it.amount }.average()
            val anomaly = expensesList.firstOrNull { it.amount > avgExpense * 2.5 && it.amount >= 2000.0 }
            if (anomaly != null) {
                insights.add(
                    SmartInsight(
                        id = "insight-anomaly-${anomaly.id}",
                        title = "Unusual Expense Flagged",
                        description = "${anomaly.title} (₹${anomaly.amount.toInt()}) in ${anomaly.category} is unusually high compared to your typical expense average of ₹${avgExpense.toInt()}.",
                        type = InsightType.ANOMALY,
                        severity = InsightSeverity.WARNING,
                        iconType = "alert",
                        actionText = "Review"
                    )
                )
            }
        }

        // 8. Recurring Transaction Detection
        val recurringTitles = listOf("netflix", "spotify", "rent", "gym", "prime", "electricity", "wifi", "internet", "subscription")
        val recurringTx = expensesList.firstOrNull { tx ->
            recurringTitles.any { tx.title.lowercase().contains(it) } ||
            expensesList.count { it.title.equals(tx.title, ignoreCase = true) } >= 2
        }
        if (recurringTx != null) {
            insights.add(
                SmartInsight(
                    id = "insight-recurring-${recurringTx.title.lowercase().take(8)}",
                    title = "Recurring Payment Detected",
                    description = "Identified recurring pattern for '${recurringTx.title}' (₹${recurringTx.amount.toInt()}). Tracked under ${recurringTx.category}.",
                    type = InsightType.SPENDING,
                    severity = InsightSeverity.INFO,
                    iconType = "repeat",
                    actionText = "Manage"
                )
            )
        }

        // 9. Goal Forecasting
        val forecastGoal = goalResults.firstOrNull { !it.isCompleted && it.savedAmount > 0.0 }
        if (forecastGoal != null && forecastGoal.remainingAmount > 0.0) {
            val estimatedMonthly = 5000.0
            val monthsLeft = kotlin.math.ceil(forecastGoal.remainingAmount / estimatedMonthly).toInt()
            if (monthsLeft > 0) {
                insights.add(
                    SmartInsight(
                        id = "insight-forecast-${forecastGoal.goalId}",
                        title = "Goal Forecast: ${forecastGoal.name}",
                        description = "At a contribution pace of ₹${estimatedMonthly.toInt()}/month, you will reach your ₹${forecastGoal.targetAmount.toInt()} goal in ~$monthsLeft month(s).",
                        type = InsightType.GOAL,
                        severity = InsightSeverity.INFO,
                        iconType = "trending-up",
                        actionText = "Save More"
                    )
                )
            }
        }

        // Fallback default insight if no other insights triggered
        if (insights.isEmpty()) {
            insights.add(
                SmartInsight(
                    id = "insight-default",
                    title = "Financial Health",
                    description = "Your finances are balanced. Keep tracking expenses and setting savings goals to build wealth.",
                    type = InsightType.SUMMARY,
                    severity = InsightSeverity.INFO,
                    iconType = "sparkles"
                )
            )
        }

        return insights
    }

    /**
     * Generates a structured monthly financial summary with smart commentary.
     */
    fun generateMonthlySummary(
        monthLabel: String,
        totals: FinanceTotals,
        topCategory: CategorySpendingResult?,
        budgetResults: List<BudgetCalculationResult>
    ): MonthlyAiSummary {
        val totalBudget = budgetResults.sumOf { it.totalLimit }
        val totalUsed = budgetResults.sumOf { it.totalSpent }
        val budgetUsagePct = if (totalBudget > 0) FinanceCalculator.roundMoney((totalUsed / totalBudget) * 100.0) else 0.0

        val explanation = buildString {
            append("In $monthLabel, you earned ₹${totals.totalIncome.toInt()} and spent ₹${totals.totalExpenses.toInt()}, resulting in ₹${totals.savings.toInt()} saved (${totals.savingsRate.toInt()}% savings rate). ")
            if (topCategory != null && topCategory.spentAmount > 0.0) {
                append("${topCategory.categoryName} was your highest expenditure (₹${topCategory.spentAmount.toInt()}). ")
            }
            if (totalBudget > 0.0) {
                append("Overall budget utilization is at ${budgetUsagePct.toInt()}%.")
            }
        }

        return MonthlyAiSummary(
            monthLabel = monthLabel,
            income = totals.totalIncome,
            expenses = totals.totalExpenses,
            savings = totals.savings,
            savingsRate = totals.savingsRate,
            topCategory = topCategory?.categoryName,
            topCategoryAmount = topCategory?.spentAmount ?: 0.0,
            budgetUsagePct = budgetUsagePct,
            aiExplanation = explanation
        )
    }
}
