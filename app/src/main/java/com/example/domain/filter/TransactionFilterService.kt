package com.example.domain.filter

import com.example.domain.model.CustomDateRange
import com.example.domain.model.DatePeriod
import com.example.domain.utils.DatePeriodUtils
import com.example.model.TransactionItem
import com.example.model.TransactionType
import java.time.LocalDate

object TransactionFilterService {

    /**
     * Filters transactions using centralized domain rules.
     */
    fun filter(
        transactions: List<TransactionItem>,
        typeFilter: String = "All", // "All", "Expenses", "Income"
        categoryFilter: String = "All",
        datePeriod: DatePeriod = DatePeriod.ALL_TIME,
        searchQuery: String = "",
        paymentMethod: String = "All",
        minAmount: Double = 0.0,
        maxAmount: Double = Double.MAX_VALUE,
        now: LocalDate = LocalDate.now(),
        customDateRange: CustomDateRange? = null
    ): List<TransactionItem> {
        val trimmedQuery = searchQuery.trim()

        return transactions.filter { tx ->
            // Type match
            val matchesType = when (typeFilter.lowercase()) {
                "expenses", "expense" -> tx.type == TransactionType.EXPENSE
                "income" -> tx.type == TransactionType.INCOME
                else -> true
            }
            if (!matchesType) return@filter false

            // Category match
            val matchesCategory = categoryFilter.equals("All", ignoreCase = true) ||
                tx.category.equals(categoryFilter, ignoreCase = true)
            if (!matchesCategory) return@filter false

            // Payment method match
            val matchesPayment = paymentMethod.equals("All", ignoreCase = true) ||
                tx.paymentMethod.equals(paymentMethod, ignoreCase = true)
            if (!matchesPayment) return@filter false

            // Amount range match
            val matchesAmount = tx.amount in minAmount..maxAmount
            if (!matchesAmount) return@filter false

            // Search query match
            val matchesSearch = trimmedQuery.isEmpty() ||
                tx.title.contains(trimmedQuery, ignoreCase = true) ||
                tx.category.contains(trimmedQuery, ignoreCase = true) ||
                tx.notes.contains(trimmedQuery, ignoreCase = true)
            if (!matchesSearch) return@filter false

            // Date period match
            DatePeriodUtils.isDateInPeriod(tx.date, datePeriod, now, customDateRange)
        }
    }
}
