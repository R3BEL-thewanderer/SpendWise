package com.example.domain.ai

import com.example.domain.model.BudgetCalculationResult
import com.example.domain.model.BudgetStatus
import com.example.domain.model.CategorySpendingResult
import com.example.domain.model.FinanceTotals
import com.example.domain.model.GoalCalculationResult
import com.example.domain.model.MonthlyTrendItem
import com.example.model.TransactionItem
import com.example.model.TransactionType

object NaturalLanguageAssistant {

    /**
     * Answers natural language financial queries using deterministic domain calculations
     * and optional Gemini AI reasoning.
     */
    suspend fun answerQuery(
        query: String,
        totals: FinanceTotals,
        categoryBreakdown: List<CategorySpendingResult>,
        budgetResults: List<BudgetCalculationResult>,
        goalResults: List<GoalCalculationResult>,
        monthlyTrends: List<MonthlyTrendItem>,
        transactions: List<TransactionItem>
    ): String {
        val q = query.trim().lowercase()

        // 1. Where did most of my money go / Top category
        if (q.contains("most of my money") || q.contains("highest") || q.contains("top spend") || q.contains("where did my money go")) {
            val top = categoryBreakdown.firstOrNull { it.spentAmount > 0.0 }
            return if (top != null && totals.totalExpenses > 0.0) {
                "Based on your recorded transactions, most of your money went to **${top.categoryName}** at **₹${top.spentAmount.toInt()}**, which makes up **${top.percentageOfTotal.toInt()}%** of your total spending (₹${totals.totalExpenses.toInt()})."
            } else {
                "You haven't recorded any expenses yet for this period, so no category dominates your spending."
            }
        }

        // 2. Specific category query (e.g. food, shopping, transport, bills, health)
        for (cat in categoryBreakdown) {
            val catName = cat.categoryName.lowercase()
            val simplifiedName = when {
                catName.contains("food") -> "food"
                catName.contains("shop") -> "shopping"
                catName.contains("transport") -> "transport"
                catName.contains("bill") || catName.contains("util") -> "bill"
                catName.contains("entertain") -> "entertainment"
                catName.contains("health") -> "health"
                catName.contains("edu") -> "education"
                else -> catName
            }

            if (q.contains(simplifiedName) && (q.contains("spent") || q.contains("spend") || q.contains("how much") || q.contains("cost"))) {
                val budget = budgetResults.flatMap { it.categoryResults }.firstOrNull { it.categoryName.equals(cat.categoryName, ignoreCase = true) }
                val budgetNote = if (budget != null) {
                    val statusText = when (budget.status) {
                        BudgetStatus.OVER_BUDGET -> "⚠️ (Over budget by ₹${budget.overBudgetAmount.toInt()})"
                        BudgetStatus.NEAR_LIMIT -> "⚠️ (${budget.usagePercentage.toInt()}% of ₹${budget.allocatedLimit.toInt()} limit)"
                        BudgetStatus.UNDER_BUDGET -> "✅ (${budget.usagePercentage.toInt()}% of ₹${budget.allocatedLimit.toInt()} limit)"
                    }
                    " $statusText"
                } else ""

                return "You have spent **₹${cat.spentAmount.toInt()}** across **${cat.transactionCount}** transaction(s) in **${cat.categoryName}**, accounting for **${cat.percentageOfTotal.toInt()}%** of total expenses.$budgetNote"
            }
        }

        // 3. Comparison with last month / Trend
        if (q.contains("last month") || q.contains("more than") || q.contains("spending trend") || q.contains("compare")) {
            if (monthlyTrends.size >= 2) {
                val current = monthlyTrends.last()
                val prev = monthlyTrends[monthlyTrends.size - 2]
                val diff = current.expenses - prev.expenses
                return if (diff > 0) {
                    val pct = if (prev.expenses > 0) ((diff / prev.expenses) * 100).toInt() else 0
                    "Yes, you have spent **₹${current.expenses.toInt()}** this month compared to **₹${prev.expenses.toInt()}** in ${prev.monthLabel} (+₹${diff.toInt()}, a **$pct% increase**)."
                } else if (diff < 0) {
                    val pct = if (prev.expenses > 0) (((-diff) / prev.expenses) * 100).toInt() else 0
                    "No, you are actually spending less! You spent **₹${current.expenses.toInt()}** this month versus **₹${prev.expenses.toInt()}** in ${prev.monthLabel} (a savings of **₹${(-diff).toInt()}**, or **$pct% reduction**)."
                } else {
                    "Your spending this month (₹${current.expenses.toInt()}) is identical to ${prev.monthLabel}."
                }
            } else {
                return "You have spent ₹${totals.totalExpenses.toInt()} this month. Additional historical data is needed to compare with previous months."
            }
        }

        // 4. Budget status
        if (q.contains("budget") || q.contains("on track") || q.contains("limit")) {
            val b = budgetResults.firstOrNull()
            return if (b != null) {
                val statusText = when (b.status) {
                    BudgetStatus.OVER_BUDGET -> "⚠️ **Over Budget**: You have exceeded your limit by ₹${b.overBudgetAmount.toInt()}."
                    BudgetStatus.NEAR_LIMIT -> "⚠️ **Near Limit**: You have used ${b.usagePercentage.toInt()}% of your budget (₹${b.remainingBudget.toInt()} remaining)."
                    BudgetStatus.UNDER_BUDGET -> "✅ **On Track**: You have used ${b.usagePercentage.toInt()}% of your ₹${b.totalLimit.toInt()} budget with ₹${b.remainingBudget.toInt()} remaining."
                }
                "For **${b.name}** (${b.month}), you have spent **₹${b.totalSpent.toInt()}** out of **₹${b.totalLimit.toInt()}**.\n\n$statusText"
            } else {
                "You haven't set up an active budget yet. Tap 'Create Budget' in the Budgets tab to track your spending limits!"
            }
        }

        // 5. Balance / Cash / Money status
        if (q.contains("balance") || q.contains("how much money") || q.contains("cash flow") || q.contains("savings")) {
            return "Your current balance is **₹${totals.balance.toInt()}** (Total Income: ₹${totals.totalIncome.toInt()}, Total Expenses: ₹${totals.totalExpenses.toInt()}). Your savings rate is **${totals.savingsRate.toInt()}%** with net savings of **₹${totals.savings.toInt()}**."
        }

        // 6. Savings Goals / Gulak / Goal Forecasting
        if (q.contains("goal") || q.contains("gulak") || q.contains("saving for") || q.contains("forecast") || q.contains("how long")) {
            if (goalResults.isNotEmpty()) {
                val sb = StringBuilder("Here is the status of your savings goals:\n\n")
                goalResults.forEach { g ->
                    val status = if (g.isCompleted) "🎉 Complete!" else "${g.progressPercent.toInt()}% (₹${g.remainingAmount.toInt()} left)"
                    val forecast = if (!g.isCompleted && g.remainingAmount > 0) {
                        val estMonths = kotlin.math.ceil(g.remainingAmount / 5000.0).toInt()
                        " (Est. ~$estMonths mo at ₹5k/mo)"
                    } else ""
                    sb.append("• **${g.name}**: ₹${g.savedAmount.toInt()} / ₹${g.targetAmount.toInt()} — $status$forecast\n")
                }
                return sb.toString().trim()
            } else {
                return "You don't have any savings goals yet. You can create one in the Goals tab to track your Digital Gulak progress!"
            }
        }

        // 7. Recurring transactions & Subscriptions
        if (q.contains("recurring") || q.contains("subscription") || q.contains("bill") || q.contains("netflix") || q.contains("spotify")) {
            val recurringKeywords = listOf("netflix", "spotify", "rent", "gym", "prime", "electricity", "wifi", "subscription")
            val matches = transactions.filter { tx ->
                recurringKeywords.any { tx.title.lowercase().contains(it) } ||
                transactions.count { it.title.equals(tx.title, ignoreCase = true) } >= 2
            }.distinctBy { it.title.lowercase() }

            return if (matches.isNotEmpty()) {
                val list = matches.joinToString("\n") { "• **${it.title}**: ₹${it.amount.toInt()} (${it.category})" }
                "Here are your detected recurring payments and subscriptions:\n\n$list"
            } else {
                "No recurring subscription patterns detected in your recent records."
            }
        }

        // 8. Unusually high spending / Anomalies
        if (q.contains("unusual") || q.contains("anomaly") || q.contains("high spend") || q.contains("spike")) {
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            if (expenses.isNotEmpty()) {
                val avg = expenses.map { it.amount }.average()
                val highSpends = expenses.filter { it.amount > avg * 2.0 }
                return if (highSpends.isNotEmpty()) {
                    val list = highSpends.joinToString("\n") { "• **${it.title}**: ₹${it.amount.toInt()} (${it.category})" }
                    "Found ${highSpends.size} expense(s) significantly higher than your average (₹${avg.toInt()}):\n\n$list"
                } else {
                    "All your recent expenses are within normal spending patterns (average: ₹${avg.toInt()})."
                }
            }
        }

        // 9. If Gemini API is configured, use Gemini with verified domain numbers
        if (GeminiClient.isKeyConfigured()) {
            val systemContext = """
                You are SpendWise AI, an intelligent personal finance assistant for the SpendWise Android app.
                You are provided with VERIFIED financial calculations from the domain layer.
                NEVER recalculate or hallucinate financial numbers. ONLY explain the verified facts.
                Financial Facts:
                - Total Balance: ₹${totals.balance.toInt()}
                - Monthly Income: ₹${totals.totalIncome.toInt()}
                - Monthly Expenses: ₹${totals.totalExpenses.toInt()}
                - Net Savings: ₹${totals.savings.toInt()} (${totals.savingsRate.toInt()}% savings rate)
                - Top Category: ${categoryBreakdown.firstOrNull()?.categoryName ?: "None"} (₹${categoryBreakdown.firstOrNull()?.spentAmount?.toInt() ?: 0})
                - Active Budgets: ${budgetResults.joinToString { "${it.name}: ₹${it.totalSpent.toInt()}/₹${it.totalLimit.toInt()} (${it.status})" }}
                - Savings Goals: ${goalResults.joinToString { "${it.name}: ₹${it.savedAmount.toInt()}/₹${it.targetAmount.toInt()}" }}
                
                Respond in a friendly, concise, financial advisor tone. Keep answers under 3-4 sentences.
            """.trimIndent()

            val aiResponse = GeminiClient.generateFinancialAdvice(systemContext, query)
            if (!aiResponse.isNullOrBlank()) {
                return aiResponse.trim()
            }
        }

        // Fallback default response
        return "You have recorded **₹${totals.totalIncome.toInt()}** in income and **₹${totals.totalExpenses.toInt()}** in expenses, leaving a current balance of **₹${totals.balance.toInt()}**. You can ask about specific categories (e.g. food, shopping), budget status, or comparisons to last month!"
    }
}
