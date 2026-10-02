package com.example.domain.calculator

import com.example.domain.model.BudgetCalculationResult
import com.example.domain.model.BudgetStatus
import com.example.domain.model.CategoryBudgetResult
import com.example.domain.utils.DatePeriodUtils
import com.example.model.BudgetItem
import com.example.model.CategoryAllocation
import com.example.model.TransactionItem
import com.example.model.TransactionType
import java.time.LocalDate

object BudgetCalculator {

    /**
     * Calculates the spending for a category allocation within a budget's month.
     */
    fun calculateCategoryBudget(
        allocation: CategoryAllocation,
        transactions: List<TransactionItem>,
        budgetMonth: String,
        alertThresholdPercent: Int = 90,
        now: LocalDate = LocalDate.now()
    ): CategoryBudgetResult {
        val matchingExpenses = transactions.filter { tx ->
            tx.type == TransactionType.EXPENSE &&
                tx.category.equals(allocation.categoryName, ignoreCase = true) &&
                DatePeriodUtils.isDateInBudgetMonth(tx.date, budgetMonth, now)
        }

        val spent = FinanceCalculator.roundMoney(matchingExpenses.sumOf { it.amount })
        val limit = allocation.amount
        val remaining = FinanceCalculator.roundMoney(limit - spent)
        val usagePct = if (limit > 0.0) FinanceCalculator.roundMoney((spent / limit) * 100.0) else 0.0
        val remainingPct = FinanceCalculator.roundMoney(100.0 - usagePct)
        val overBudget = if (spent > limit) FinanceCalculator.roundMoney(spent - limit) else 0.0

        val status = when {
            spent > limit -> BudgetStatus.OVER_BUDGET
            usagePct >= alertThresholdPercent -> BudgetStatus.NEAR_LIMIT
            else -> BudgetStatus.UNDER_BUDGET
        }

        return CategoryBudgetResult(
            categoryName = allocation.categoryName,
            allocatedLimit = limit,
            spent = spent,
            remaining = remaining,
            usagePercentage = usagePct,
            remainingPercentage = remainingPct,
            overBudgetAmount = overBudget,
            status = status
        )
    }

    /**
     * Calculates complete budget metrics from actual transactions.
     * Formula:
     * - Budget Used = sum of applicable expenses for this budget's period
     * - Remaining = totalLimit - spent
     * - Usage % = (spent / totalLimit) * 100
     * - Remaining % = 100 - usage %
     * - Over Budget Amount = max(0, spent - totalLimit)
     * - Status = OVER_BUDGET / NEAR_LIMIT / UNDER_BUDGET
     */
    fun calculateBudget(
        budget: BudgetItem,
        transactions: List<TransactionItem>,
        now: LocalDate = LocalDate.now()
    ): BudgetCalculationResult {
        // Applicable expenses are all expenses that fall into this budget's month
        val periodExpenses = transactions.filter { tx ->
            tx.type == TransactionType.EXPENSE &&
                DatePeriodUtils.isDateInBudgetMonth(tx.date, budget.month, now)
        }

        // Calculate total spent for this budget period
        // If the budget has allocations, calculate category-specific results
        val categoryResults = budget.allocations.map { alloc ->
            calculateCategoryBudget(
                allocation = alloc,
                transactions = transactions,
                budgetMonth = budget.month,
                alertThresholdPercent = budget.alertThresholdPercent,
                now = now
            )
        }

        val totalSpentFromTxs = periodExpenses.sumOf { it.amount }
        // If there are transactions in period, use them; if no transactions exist but budget.spent is set, use transactions source of truth
        val totalSpent = FinanceCalculator.roundMoney(totalSpentFromTxs)
        val limit = budget.totalLimit
        val remaining = FinanceCalculator.roundMoney(limit - totalSpent)
        val usagePct = if (limit > 0.0) FinanceCalculator.roundMoney((totalSpent / limit) * 100.0) else 0.0
        val remainingPct = FinanceCalculator.roundMoney(100.0 - usagePct)
        val overBudget = if (totalSpent > limit) FinanceCalculator.roundMoney(totalSpent - limit) else 0.0

        val status = when {
            totalSpent > limit -> BudgetStatus.OVER_BUDGET
            usagePct >= budget.alertThresholdPercent -> BudgetStatus.NEAR_LIMIT
            else -> BudgetStatus.UNDER_BUDGET
        }

        return BudgetCalculationResult(
            budgetId = budget.id,
            name = budget.name,
            month = budget.month,
            totalLimit = limit,
            totalSpent = totalSpent,
            remainingBudget = remaining,
            usagePercentage = usagePct,
            remainingPercentage = remainingPct,
            overBudgetAmount = overBudget,
            status = status,
            categoryResults = categoryResults
        )
    }
}
