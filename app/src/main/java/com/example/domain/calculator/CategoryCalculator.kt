package com.example.domain.calculator

import com.example.domain.model.CategorySpendingResult
import com.example.model.CategoryItem
import com.example.model.TransactionItem
import com.example.model.TransactionType

object CategoryCalculator {

    /**
     * Calculates total spending for a specific category.
     * Formula: Sum of expenses belonging to that category.
     */
    fun calculateCategorySpending(categoryName: String, transactions: List<TransactionItem>): Double {
        val total = transactions
            .filter { it.type == TransactionType.EXPENSE && it.category.equals(categoryName, ignoreCase = true) }
            .sumOf { it.amount }
        return FinanceCalculator.roundMoney(total)
    }

    /**
     * Calculates category expense percentage of total expenses.
     * Formula: (Category Spending / Total Expenses) * 100
     * Handles Total Expenses == 0 safely.
     */
    fun calculateCategoryExpensePercentage(categorySpent: Double, totalExpenses: Double): Double {
        if (totalExpenses <= 0.0 || categorySpent <= 0.0) return 0.0
        val percent = (categorySpent / totalExpenses) * 100.0
        return FinanceCalculator.roundMoney(percent)
    }

    /**
     * Generates a complete category spending breakdown for all categories based on transactions.
     */
    fun calculateCategoryBreakdown(
        categories: List<CategoryItem>,
        transactions: List<TransactionItem>
    ): List<CategorySpendingResult> {
        val totalExpenses = FinanceCalculator.calculateExpenses(transactions)

        return categories.map { cat ->
            val catTransactions = transactions.filter {
                it.type == TransactionType.EXPENSE && it.category.equals(cat.name, ignoreCase = true)
            }
            val spent = FinanceCalculator.roundMoney(catTransactions.sumOf { it.amount })
            val pct = calculateCategoryExpensePercentage(spent, totalExpenses)

            CategorySpendingResult(
                categoryName = cat.name,
                spentAmount = spent,
                percentageOfTotal = pct,
                transactionCount = catTransactions.size
            )
        }.sortedByDescending { it.spentAmount }
    }
}
