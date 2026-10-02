package com.example.domain.calculator

import com.example.domain.model.CustomDateRange
import com.example.domain.model.DatePeriod
import com.example.domain.model.FinanceTotals
import com.example.domain.utils.DatePeriodUtils
import com.example.model.TransactionItem
import com.example.model.TransactionType
import java.time.LocalDate
import kotlin.math.round

object FinanceCalculator {

    /**
     * Rounds monetary value to 2 decimal places to prevent floating-point precision issues.
     */
    fun roundMoney(value: Double): Double {
        return round(value * 100.0) / 100.0
    }

    /**
     * Calculates total income from a list of transactions.
     * Formula: Sum of all income transaction amounts.
     */
    fun calculateIncome(transactions: List<TransactionItem>): Double {
        val total = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        return roundMoney(total)
    }

    /**
     * Calculates total expenses from a list of transactions.
     * Formula: Sum of all expense transaction amounts.
     */
    fun calculateExpenses(transactions: List<TransactionItem>): Double {
        val total = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        return roundMoney(total)
    }

    /**
     * Calculates total balance.
     * Formula: Total Income - Total Expenses.
     */
    fun calculateBalance(transactions: List<TransactionItem>): Double {
        val income = calculateIncome(transactions)
        val expenses = calculateExpenses(transactions)
        return calculateBalance(income, expenses)
    }

    /**
     * Overload for calculating balance given pre-computed income and expenses.
     */
    fun calculateBalance(income: Double, expenses: Double): Double {
        return roundMoney(income - expenses)
    }

    /**
     * Calculates Net Cash Flow.
     * Formula: Income - Expenses.
     */
    fun calculateNetCashFlow(income: Double, expenses: Double): Double {
        return roundMoney(income - expenses)
    }

    /**
     * Calculates Savings.
     * Formula: Income - Expenses.
     */
    fun calculateSavings(income: Double, expenses: Double): Double {
        return roundMoney(income - expenses)
    }

    /**
     * Calculates Savings Rate %.
     * Formula: ((Income - Expenses) / Income) * 100
     * Handles Income <= 0 safely without division by zero.
     */
    fun calculateSavingsRate(income: Double, expenses: Double): Double {
        if (income <= 0.0) return 0.0
        val rate = ((income - expenses) / income) * 100.0
        return roundMoney(rate)
    }

    /**
     * Computes all financial totals into a unified domain result object.
     */
    fun calculateTotals(transactions: List<TransactionItem>): FinanceTotals {
        val income = calculateIncome(transactions)
        val expenses = calculateExpenses(transactions)
        val balance = calculateBalance(income, expenses)
        val netCashFlow = calculateNetCashFlow(income, expenses)
        val savings = calculateSavings(income, expenses)
        val savingsRate = calculateSavingsRate(income, expenses)

        return FinanceTotals(
            totalIncome = income,
            totalExpenses = expenses,
            balance = balance,
            netCashFlow = netCashFlow,
            savings = savings,
            savingsRate = savingsRate
        )
    }

    /**
     * Computes period-filtered totals (All Time, Current Week, Current Month, Previous Month, Custom).
     */
    fun calculatePeriodTotals(
        transactions: List<TransactionItem>,
        period: DatePeriod,
        now: LocalDate = LocalDate.now(),
        customRange: CustomDateRange? = null
    ): FinanceTotals {
        val filtered = transactions.filter { tx ->
            DatePeriodUtils.isDateInPeriod(tx.date, period, now, customRange)
        }
        return calculateTotals(filtered)
    }
}
