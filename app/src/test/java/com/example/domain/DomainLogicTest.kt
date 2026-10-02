package com.example.domain

import com.example.domain.calculator.AnalyticsCalculator
import com.example.domain.calculator.BudgetCalculator
import com.example.domain.calculator.CategoryCalculator
import com.example.domain.calculator.FinanceCalculator
import com.example.domain.calculator.GoalCalculator
import com.example.domain.filter.TransactionFilterService
import com.example.domain.model.BudgetStatus
import com.example.domain.model.CustomDateRange
import com.example.domain.model.DatePeriod
import com.example.domain.model.ValidationResult
import com.example.domain.utils.DatePeriodUtils
import com.example.domain.validation.FinancialValidator
import com.example.model.BudgetItem
import com.example.model.CategoryAllocation
import com.example.model.CategoryItem
import com.example.model.GoalContribution
import com.example.model.GoalItem
import com.example.model.TransactionItem
import com.example.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DomainLogicTest {

    // Helper to generate transaction
    private fun createTx(
        id: String = "tx-1",
        amount: Double,
        type: TransactionType,
        category: String = "Food",
        date: String = "12 Feb 2025"
    ): TransactionItem {
        return TransactionItem(
            id = id,
            title = "Test $id",
            subtitle = category,
            amount = amount,
            type = type,
            category = category,
            date = date
        )
    }

    // ==========================================
    // 1. Finance Tests
    // ==========================================

    @Test
    fun `test total income calculation`() {
        val txs = listOf(
            createTx("1", 50000.0, TransactionType.INCOME),
            createTx("2", 10000.0, TransactionType.INCOME),
            createTx("3", 5000.0, TransactionType.INCOME),
            createTx("4", 2000.0, TransactionType.EXPENSE)
        )
        val income = FinanceCalculator.calculateIncome(txs)
        assertEquals(65000.0, income, 0.001)
    }

    @Test
    fun `test total expenses calculation`() {
        val txs = listOf(
            createTx("1", 5000.0, TransactionType.EXPENSE),
            createTx("2", 3000.0, TransactionType.EXPENSE),
            createTx("3", 7000.0, TransactionType.EXPENSE),
            createTx("4", 10000.0, TransactionType.INCOME)
        )
        val expenses = FinanceCalculator.calculateExpenses(txs)
        assertEquals(15000.0, expenses, 0.001)
    }

    @Test
    fun `test balance and net cash flow calculation`() {
        val income = 60000.0
        val expenses = 15000.0
        val balance = FinanceCalculator.calculateBalance(income, expenses)
        val netCashFlow = FinanceCalculator.calculateNetCashFlow(income, expenses)

        assertEquals(45000.0, balance, 0.001)
        assertEquals(45000.0, netCashFlow, 0.001)
    }

    @Test
    fun `test empty transaction list returns zero`() {
        val totals = FinanceCalculator.calculateTotals(emptyList())
        assertEquals(0.0, totals.totalIncome, 0.001)
        assertEquals(0.0, totals.totalExpenses, 0.001)
        assertEquals(0.0, totals.balance, 0.001)
        assertEquals(0.0, totals.netCashFlow, 0.001)
        assertEquals(0.0, totals.savings, 0.001)
        assertEquals(0.0, totals.savingsRate, 0.001)
    }

    @Test
    fun `test only income transactions`() {
        val txs = listOf(createTx("1", 5000.0, TransactionType.INCOME))
        val totals = FinanceCalculator.calculateTotals(txs)
        assertEquals(5000.0, totals.totalIncome, 0.001)
        assertEquals(0.0, totals.totalExpenses, 0.001)
        assertEquals(5000.0, totals.balance, 0.001)
        assertEquals(100.0, totals.savingsRate, 0.001)
    }

    @Test
    fun `test only expense transactions`() {
        val txs = listOf(createTx("1", 3500.0, TransactionType.EXPENSE))
        val totals = FinanceCalculator.calculateTotals(txs)
        assertEquals(0.0, totals.totalIncome, 0.001)
        assertEquals(3500.0, totals.totalExpenses, 0.001)
        assertEquals(-3500.0, totals.balance, 0.001)
        assertEquals(0.0, totals.savingsRate, 0.001) // Safe 0 when income <= 0
    }

    @Test
    fun `test income equal to expenses`() {
        val txs = listOf(
            createTx("1", 10000.0, TransactionType.INCOME),
            createTx("2", 10000.0, TransactionType.EXPENSE)
        )
        val totals = FinanceCalculator.calculateTotals(txs)
        assertEquals(0.0, totals.balance, 0.001)
        assertEquals(0.0, totals.savings, 0.001)
        assertEquals(0.0, totals.savingsRate, 0.001)
    }

    @Test
    fun `test expenses greater than income`() {
        val txs = listOf(
            createTx("1", 10000.0, TransactionType.INCOME),
            createTx("2", 15000.0, TransactionType.EXPENSE)
        )
        val totals = FinanceCalculator.calculateTotals(txs)
        assertEquals(-5000.0, totals.balance, 0.001)
        assertEquals(-5000.0, totals.savings, 0.001)
        assertEquals(-50.0, totals.savingsRate, 0.001)
    }

    // ==========================================
    // 2. Category Tests
    // ==========================================

    @Test
    fun `test category spending and percentage`() {
        val txs = listOf(
            createTx("1", 5000.0, TransactionType.EXPENSE, category = "Food"),
            createTx("2", 15000.0, TransactionType.EXPENSE, category = "Shopping")
        )
        val foodSpending = CategoryCalculator.calculateCategorySpending("Food", txs)
        val totalExpenses = FinanceCalculator.calculateExpenses(txs)
        val foodPercentage = CategoryCalculator.calculateCategoryExpensePercentage(foodSpending, totalExpenses)

        assertEquals(5000.0, foodSpending, 0.001)
        assertEquals(20000.0, totalExpenses, 0.001)
        assertEquals(25.0, foodPercentage, 0.001)
    }

    @Test
    fun `test category percentage with zero expenses does not crash`() {
        val pct = CategoryCalculator.calculateCategoryExpensePercentage(0.0, 0.0)
        assertEquals(0.0, pct, 0.001)
    }

    @Test
    fun `test category with no transactions returns zero`() {
        val txs = listOf(createTx("1", 1000.0, TransactionType.EXPENSE, category = "Travel"))
        val foodSpending = CategoryCalculator.calculateCategorySpending("Food", txs)
        assertEquals(0.0, foodSpending, 0.001)
    }

    // ==========================================
    // 3. Budget Tests
    // ==========================================

    @Test
    fun `test budget under limit`() {
        val txs = listOf(
            createTx("1", 6000.0, TransactionType.EXPENSE, category = "Food", date = "10 Mar 2025")
        )
        val budget = BudgetItem(
            id = "b1",
            name = "March Budget",
            month = "March 2025",
            totalLimit = 10000.0,
            spent = 0.0,
            alertThresholdPercent = 90
        )
        val result = BudgetCalculator.calculateBudget(budget, txs, now = LocalDate.of(2025, 3, 15))

        assertEquals(6000.0, result.totalSpent, 0.001)
        assertEquals(4000.0, result.remainingBudget, 0.001)
        assertEquals(60.0, result.usagePercentage, 0.001)
        assertEquals(40.0, result.remainingPercentage, 0.001)
        assertEquals(0.0, result.overBudgetAmount, 0.001)
        assertEquals(BudgetStatus.UNDER_BUDGET, result.status)
    }

    @Test
    fun `test budget exactly at limit`() {
        val txs = listOf(
            createTx("1", 10000.0, TransactionType.EXPENSE, date = "10 Mar 2025")
        )
        val budget = BudgetItem(
            id = "b1",
            name = "March Budget",
            month = "March 2025",
            totalLimit = 10000.0,
            spent = 0.0
        )
        val result = BudgetCalculator.calculateBudget(budget, txs, now = LocalDate.of(2025, 3, 15))

        assertEquals(10000.0, result.totalSpent, 0.001)
        assertEquals(0.0, result.remainingBudget, 0.001)
        assertEquals(100.0, result.usagePercentage, 0.001)
        assertEquals(0.0, result.remainingPercentage, 0.001)
        assertEquals(BudgetStatus.NEAR_LIMIT, result.status)
    }

    @Test
    fun `test budget over limit`() {
        val txs = listOf(
            createTx("1", 12000.0, TransactionType.EXPENSE, date = "10 Mar 2025")
        )
        val budget = BudgetItem(
            id = "b1",
            name = "March Budget",
            month = "March 2025",
            totalLimit = 10000.0,
            spent = 0.0
        )
        val result = BudgetCalculator.calculateBudget(budget, txs, now = LocalDate.of(2025, 3, 15))

        assertEquals(12000.0, result.totalSpent, 0.001)
        assertEquals(-2000.0, result.remainingBudget, 0.001)
        assertEquals(120.0, result.usagePercentage, 0.001)
        assertEquals(2000.0, result.overBudgetAmount, 0.001)
        assertEquals(BudgetStatus.OVER_BUDGET, result.status)
    }

    @Test
    fun `test budget time period filtering excludes other months`() {
        val txs = listOf(
            createTx("1", 3000.0, TransactionType.EXPENSE, date = "10 Mar 2025"), // In period
            createTx("2", 5000.0, TransactionType.EXPENSE, date = "10 Apr 2025")  // Outside period
        )
        val budget = BudgetItem(
            id = "b1",
            name = "March Budget",
            month = "March 2025",
            totalLimit = 10000.0,
            spent = 0.0
        )
        val result = BudgetCalculator.calculateBudget(budget, txs, now = LocalDate.of(2025, 3, 15))
        assertEquals(3000.0, result.totalSpent, 0.001)
    }

    @Test
    fun `test category-specific budget allocation filtering`() {
        val txs = listOf(
            createTx("1", 4000.0, TransactionType.EXPENSE, category = "Food", date = "10 Mar 2025"),
            createTx("2", 2000.0, TransactionType.EXPENSE, category = "Transport", date = "10 Mar 2025"),
            createTx("3", 3000.0, TransactionType.EXPENSE, category = "Shopping", date = "10 Mar 2025")
        )
        val alloc = CategoryAllocation("Food", 50, 5000.0)
        val catResult = BudgetCalculator.calculateCategoryBudget(
            allocation = alloc,
            transactions = txs,
            budgetMonth = "March 2025",
            now = LocalDate.of(2025, 3, 15)
        )

        assertEquals(4000.0, catResult.spent, 0.001)
        assertEquals(1000.0, catResult.remaining, 0.001)
        assertEquals(80.0, catResult.usagePercentage, 0.001)
        assertEquals(BudgetStatus.UNDER_BUDGET, catResult.status)
    }

    // ==========================================
    // 4. Savings Goal & Digital Gulak Tests
    // ==========================================

    @Test
    fun `test empty goal progress`() {
        val goal = GoalItem(
            id = "g1",
            name = "Emergency Fund",
            targetAmount = 50000.0,
            currentSavings = 0.0,
            targetDate = "31 Dec 2026",
            category = "Savings"
        )
        val result = GoalCalculator.calculateGoal(goal)

        assertEquals(0.0, result.savedAmount, 0.001)
        assertEquals(50000.0, result.remainingAmount, 0.001)
        assertEquals(0.0, result.progressPercent, 0.001)
        assertEquals(0.0f, result.gulakFillRatio, 0.001f)
        assertFalse(result.isCompleted)
    }

    @Test
    fun `test partial goal progress`() {
        val goal = GoalItem(
            id = "g1",
            name = "Emergency Fund",
            targetAmount = 50000.0,
            currentSavings = 20000.0,
            targetDate = "31 Dec 2026",
            category = "Savings",
            contributions = listOf(
                GoalContribution("c1", 15000.0, "1 Jan 2026"),
                GoalContribution("c2", 5000.0, "15 Jan 2026")
            )
        )
        val result = GoalCalculator.calculateGoal(goal)

        assertEquals(20000.0, result.savedAmount, 0.001)
        assertEquals(30000.0, result.remainingAmount, 0.001)
        assertEquals(40.0, result.progressPercent, 0.001)
        assertEquals(0.40f, result.gulakFillRatio, 0.001f)
        assertFalse(result.isCompleted)
    }

    @Test
    fun `test completed goal`() {
        val goal = GoalItem(
            id = "g1",
            name = "Laptop",
            targetAmount = 50000.0,
            currentSavings = 50000.0,
            targetDate = "31 Dec 2026",
            category = "Electronics"
        )
        val result = GoalCalculator.calculateGoal(goal)

        assertEquals(50000.0, result.savedAmount, 0.001)
        assertEquals(0.0, result.remainingAmount, 0.001)
        assertEquals(100.0, result.progressPercent, 0.001)
        assertEquals(1.0f, result.gulakFillRatio, 0.001f)
        assertTrue(result.isCompleted)
    }

    @Test
    fun `test over target goal clamps gulak to 1_0`() {
        val goal = GoalItem(
            id = "g1",
            name = "Trip",
            targetAmount = 50000.0,
            currentSavings = 55000.0,
            targetDate = "31 Dec 2026",
            category = "Travel"
        )
        val result = GoalCalculator.calculateGoal(goal)

        assertEquals(55000.0, result.savedAmount, 0.001)
        assertEquals(0.0, result.remainingAmount, 0.001)
        assertEquals(110.0, result.progressPercent, 0.001)
        assertEquals(1.0f, result.gulakFillRatio, 0.001f)
        assertTrue(result.isCompleted)
    }

    // ==========================================
    // 5. Savings & Savings Rate Tests
    // ==========================================

    @Test
    fun `test savings and savings rate`() {
        val income = 50000.0
        val expenses = 35000.0
        val savings = FinanceCalculator.calculateSavings(income, expenses)
        val rate = FinanceCalculator.calculateSavingsRate(income, expenses)

        assertEquals(15000.0, savings, 0.001)
        assertEquals(30.0, rate, 0.001)
    }

    @Test
    fun `test savings rate when income is zero`() {
        val rate = FinanceCalculator.calculateSavingsRate(0.0, 500.0)
        assertEquals(0.0, rate, 0.001)
    }

    // ==========================================
    // 6. Date Boundaries & Leap Years Tests
    // ==========================================

    @Test
    fun `test date parsing and boundaries`() {
        val refDate = LocalDate.of(2024, 2, 29) // Leap year 2024!
        val d1 = DatePeriodUtils.parseDate("1 Jan 2024", refDate)
        assertEquals(LocalDate.of(2024, 1, 1), d1)

        val d2 = DatePeriodUtils.parseDate("31 Jan 2024", refDate)
        assertEquals(LocalDate.of(2024, 1, 31), d2)

        val leapDay = DatePeriodUtils.parseDate("29 Feb 2024", refDate)
        assertEquals(LocalDate.of(2024, 2, 29), leapDay)

        val nonLeapFeb = DatePeriodUtils.parseDate("28 Feb 2025", refDate)
        assertEquals(LocalDate.of(2025, 2, 28), nonLeapFeb)

        val dec31 = DatePeriodUtils.parseDate("31 Dec 2025", refDate)
        assertEquals(LocalDate.of(2025, 12, 31), dec31)

        val jan1Next = DatePeriodUtils.parseDate("1 Jan 2026", refDate)
        assertEquals(LocalDate.of(2026, 1, 1), jan1Next)
    }

    @Test
    fun `test custom date range filter`() {
        val range = CustomDateRange("1 Feb 2025", "15 Feb 2025")
        assertTrue(DatePeriodUtils.isDateInPeriod("10 Feb 2025", DatePeriod.CUSTOM, customRange = range))
        assertTrue(DatePeriodUtils.isDateInPeriod("1 Feb 2025", DatePeriod.CUSTOM, customRange = range))
        assertTrue(DatePeriodUtils.isDateInPeriod("15 Feb 2025", DatePeriod.CUSTOM, customRange = range))
        assertFalse(DatePeriodUtils.isDateInPeriod("16 Feb 2025", DatePeriod.CUSTOM, customRange = range))
        assertFalse(DatePeriodUtils.isDateInPeriod("31 Jan 2025", DatePeriod.CUSTOM, customRange = range))
    }

    // ==========================================
    // 7. Validation Tests
    // ==========================================

    @Test
    fun `test transaction validation rules`() {
        // Valid
        val valid = FinancialValidator.validateTransaction(500.0, TransactionType.EXPENSE, "Food", "Lunch", "Today")
        assertTrue(valid.isValid)

        // Invalid zero amount
        val zeroAmt = FinancialValidator.validateTransaction(0.0, TransactionType.EXPENSE, "Food", "Lunch", "Today")
        assertFalse(zeroAmt.isValid)
        assertTrue(zeroAmt is ValidationResult.Invalid && zeroAmt.field == "amount")

        // Invalid negative amount
        val negAmt = FinancialValidator.validateTransaction(-10.0, TransactionType.EXPENSE, "Food", "Lunch", "Today")
        assertFalse(negAmt.isValid)

        // Invalid blank title
        val blankTitle = FinancialValidator.validateTransaction(100.0, TransactionType.EXPENSE, "Food", "   ", "Today")
        assertFalse(blankTitle.isValid)
    }

    @Test
    fun `test budget validation rules`() {
        assertTrue(FinancialValidator.validateBudget("April Budget", 20000.0, "April 2025").isValid)
        assertFalse(FinancialValidator.validateBudget("", 20000.0, "April 2025").isValid)
        assertFalse(FinancialValidator.validateBudget("April Budget", 0.0, "April 2025").isValid)
    }

    @Test
    fun `test goal and contribution validation rules`() {
        assertTrue(FinancialValidator.validateGoal("New Car", 500000.0).isValid)
        assertFalse(FinancialValidator.validateGoal("", 500000.0).isValid)
        assertFalse(FinancialValidator.validateGoal("New Car", 0.0).isValid)

        assertTrue(FinancialValidator.validateContribution(1000.0, goalExists = true).isValid)
        assertFalse(FinancialValidator.validateContribution(0.0, goalExists = true).isValid)
        assertFalse(FinancialValidator.validateContribution(1000.0, goalExists = false).isValid)
    }

    // ==========================================
    // 8. Analytics & Monthly Trends Tests
    // ==========================================

    @Test
    fun `test monthly trend aggregation`() {
        val now = LocalDate.of(2025, 3, 15)
        val txs = listOf(
            createTx("1", 45000.0, TransactionType.INCOME, date = "1 Mar 2025"),
            createTx("2", 15000.0, TransactionType.EXPENSE, date = "10 Mar 2025"),
            createTx("3", 40000.0, TransactionType.INCOME, date = "1 Feb 2025"),
            createTx("4", 12000.0, TransactionType.EXPENSE, date = "15 Feb 2025")
        )
        val trends = AnalyticsCalculator.calculateMonthlyTrends(txs, now, monthCount = 3)
        assertEquals(3, trends.size)

        val marTrend = trends.first { it.monthLabel == "Mar" }
        assertEquals(45000.0, marTrend.income, 0.001)
        assertEquals(15000.0, marTrend.expenses, 0.001)
        assertEquals(30000.0, marTrend.net, 0.001)

        val febTrend = trends.first { it.monthLabel == "Feb" }
        assertEquals(40000.0, febTrend.income, 0.001)
        assertEquals(12000.0, febTrend.expenses, 0.001)
        assertEquals(28000.0, febTrend.net, 0.001)
    }
}
