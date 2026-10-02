package com.example.domain

import com.example.domain.ai.NaturalLanguageAssistant
import com.example.domain.ai.SmartInsightsEngine
import com.example.domain.ai.model.InsightSeverity
import com.example.domain.calculator.AnalyticsCalculator
import com.example.domain.calculator.BudgetCalculator
import com.example.domain.calculator.CategoryCalculator
import com.example.domain.calculator.FinanceCalculator
import com.example.domain.calculator.GoalCalculator
import com.example.model.BudgetItem
import com.example.model.CategoryAllocation
import com.example.model.CategoryItem
import com.example.model.GoalContribution
import com.example.model.GoalItem
import com.example.model.TransactionItem
import com.example.model.TransactionType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class Phase4AiAndSmartInsightsTest {

    private fun createTx(
        id: String,
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: String = "12 Feb 2025"
    ): TransactionItem {
        return TransactionItem(
            id = id,
            title = title,
            subtitle = category,
            amount = amount,
            type = type,
            category = category,
            date = date
        )
    }

    private val categories = listOf(
        CategoryItem("cat-1", "Food & Dining", "food", 0xFF9CC9FF, 0.0, 0),
        CategoryItem("cat-2", "Shopping", "shopping", 0xFFEF9C8D, 0.0, 0),
        CategoryItem("cat-3", "Transport", "transport", 0xFFF5D98A, 0.0, 0),
        CategoryItem("cat-4", "Subscriptions", "subscriptions", 0xFFC9B8FF, 0.0, 0),
        CategoryItem("cat-5", "Salary", "salary", 0xFFB9DEC9, 0.0, 0)
    )

    private val transactions = listOf(
        createTx("tx-1", "Monthly Salary", 60000.0, TransactionType.INCOME, "Salary", "1 Oct 2026"),
        createTx("tx-2", "Grocery Supermarket", 8500.0, TransactionType.EXPENSE, "Food & Dining", "2 Oct 2026"),
        createTx("tx-3", "Uber Rides", 2500.0, TransactionType.EXPENSE, "Transport", "3 Oct 2026"),
        createTx("tx-4", "New Clothes & Shoes", 12000.0, TransactionType.EXPENSE, "Shopping", "4 Oct 2026"),
        createTx("tx-5", "Netflix Subscription", 649.0, TransactionType.EXPENSE, "Subscriptions", "5 Oct 2026"),
        createTx("tx-6", "Spotify Music", 119.0, TransactionType.EXPENSE, "Subscriptions", "6 Oct 2026")
    )

    private val budgets = listOf(
        BudgetItem(
            id = "b-1",
            name = "October Budget",
            month = "October 2026",
            totalLimit = 30000.0,
            spent = 0.0,
            allocations = listOf(
                CategoryAllocation("Food & Dining", 30, 9000.0),
                CategoryAllocation("Shopping", 50, 15000.0),
                CategoryAllocation("Transport", 10, 3000.0)
            )
        )
    )

    private val goals = listOf(
        GoalItem(
            id = "g-1",
            name = "New Laptop",
            targetAmount = 60000.0,
            currentSavings = 48000.0,
            targetDate = "31 Dec 2026",
            category = "Electronics",
            contributions = listOf(
                GoalContribution("c-1", 48000.0, "1 Oct 2026", "Initial savings")
            )
        ),
        GoalItem(
            id = "g-2",
            name = "Goa Trip",
            targetAmount = 20000.0,
            currentSavings = 20000.0,
            targetDate = "15 Nov 2026",
            category = "Travel",
            contributions = listOf(
                GoalContribution("c-2", 20000.0, "1 Oct 2026", "Complete")
            )
        )
    )

    @Test
    fun test1_smartInsightsEngine_generatesGroundedInsights() {
        val totals = FinanceCalculator.calculateTotals(transactions)
        val catBreakdown = CategoryCalculator.calculateCategoryBreakdown(categories, transactions)
        val budgetResults = budgets.map { BudgetCalculator.calculateBudget(it, transactions, now = LocalDate.of(2026, 10, 10)) }
        val goalResults = goals.map { GoalCalculator.calculateGoal(it) }
        val trends = AnalyticsCalculator.calculateMonthlyTrends(transactions)

        val insights = SmartInsightsEngine.generateInsights(
            totals = totals,
            categoryBreakdown = catBreakdown,
            budgetResults = budgetResults,
            goalResults = goalResults,
            monthlyTrends = trends,
            transactions = transactions
        )

        assertTrue("Insights should not be empty", insights.isNotEmpty())

        // Top category insight should highlight Shopping as highest expense (₹12,000)
        val topCategoryInsight = insights.find { it.id == "insight-spending-top" }
        assertNotNull("Should have top category insight", topCategoryInsight)
        assertTrue(topCategoryInsight!!.description.contains("Shopping"))
        assertTrue(topCategoryInsight.description.contains("12000"))

        // Goal celebration insight for completed Goa Trip
        val goalCompleted = insights.find { it.id == "insight-goal-complete" }
        assertNotNull("Should celebrate completed goal", goalCompleted)
        assertTrue(goalCompleted!!.description.contains("Goa Trip"))

        // Goal progress insight for Laptop (₹12,000 remaining)
        val laptopGoal = insights.find { it.id == "insight-goal-progress" }
        assertNotNull("Should show laptop goal progress", laptopGoal)
        assertTrue(laptopGoal!!.description.contains("12000"))

        // Strong savings rate insight (₹60,000 income - ₹23,768 expenses > 20% savings)
        val savingsRateInsight = insights.find { it.id == "insight-savings-rate" }
        assertNotNull("Should recognize strong savings rate", savingsRateInsight)
        assertEquals(InsightSeverity.SUCCESS, savingsRateInsight!!.severity)

        // Recurring subscription detection (Netflix/Spotify)
        val recurringInsight = insights.find { it.id.startsWith("insight-recurring") }
        assertNotNull("Should detect recurring subscription", recurringInsight)

        // Goal forecast insight for remaining laptop goal
        val forecastInsight = insights.find { it.id.startsWith("insight-forecast") }
        assertNotNull("Should forecast remaining goal periods", forecastInsight)
    }

    @Test
    fun test2_monthlyAiSummary_structuredAndAccurate() {
        val totals = FinanceCalculator.calculateTotals(transactions)
        val catBreakdown = CategoryCalculator.calculateCategoryBreakdown(categories, transactions)
        val budgetResults = budgets.map { BudgetCalculator.calculateBudget(it, transactions, now = LocalDate.of(2026, 10, 10)) }

        val summary = SmartInsightsEngine.generateMonthlySummary(
            monthLabel = "October 2026",
            totals = totals,
            topCategory = catBreakdown.firstOrNull(),
            budgetResults = budgetResults
        )

        assertEquals("October 2026", summary.monthLabel)
        assertEquals(60000.0, summary.income, 0.01)
        assertEquals(23768.0, summary.expenses, 0.01)
        assertEquals(36232.0, summary.savings, 0.01)
        assertEquals("Shopping", summary.topCategory)
        assertEquals(12000.0, summary.topCategoryAmount, 0.01)

        assertTrue(summary.aiExplanation.contains("earned ₹60000"))
        assertTrue(summary.aiExplanation.contains("spent ₹23768"))
        assertTrue(summary.aiExplanation.contains("Shopping was your highest expenditure"))
    }

    @Test
    fun test3_naturalLanguageAssistant_answersQueriesGroundedInDomainNumbers() = runBlocking {
        val totals = FinanceCalculator.calculateTotals(transactions)
        val catBreakdown = CategoryCalculator.calculateCategoryBreakdown(categories, transactions)
        val budgetResults = budgets.map { BudgetCalculator.calculateBudget(it, transactions, now = LocalDate.of(2026, 10, 10)) }
        val goalResults = goals.map { GoalCalculator.calculateGoal(it) }
        val trends = AnalyticsCalculator.calculateMonthlyTrends(transactions)

        // 1. Where did most of my money go?
        val ans1 = NaturalLanguageAssistant.answerQuery(
            query = "Where did most of my money go this month?",
            totals = totals,
            categoryBreakdown = catBreakdown,
            budgetResults = budgetResults,
            goalResults = goalResults,
            monthlyTrends = trends,
            transactions = transactions
        )
        assertTrue("Answer 1 must specify Shopping", ans1.contains("Shopping"))
        assertTrue("Answer 1 must specify ₹12,000", ans1.contains("12000"))

        // 2. How much on food?
        val ans2 = NaturalLanguageAssistant.answerQuery(
            query = "How much have I spent on food?",
            totals = totals,
            categoryBreakdown = catBreakdown,
            budgetResults = budgetResults,
            goalResults = goalResults,
            monthlyTrends = trends,
            transactions = transactions
        )
        assertTrue("Answer 2 must state ₹8,500 on Food", ans2.contains("8500"))
        assertTrue("Answer 2 must mention Food & Dining", ans2.contains("Food & Dining"))

        // 3. How is my budget?
        val ans3 = NaturalLanguageAssistant.answerQuery(
            query = "How is my budget looking?",
            totals = totals,
            categoryBreakdown = catBreakdown,
            budgetResults = budgetResults,
            goalResults = goalResults,
            monthlyTrends = trends,
            transactions = transactions
        )
        assertTrue("Answer 3 must mention October Budget", ans3.contains("October Budget"))
        assertTrue("Answer 3 must mention total limit 30000", ans3.contains("30000"))

        // 4. Status of goals / forecast
        val ans4 = NaturalLanguageAssistant.answerQuery(
            query = "What is the status of my goals?",
            totals = totals,
            categoryBreakdown = catBreakdown,
            budgetResults = budgetResults,
            goalResults = goalResults,
            monthlyTrends = trends,
            transactions = transactions
        )
        assertTrue("Answer 4 must mention New Laptop", ans4.contains("New Laptop"))
        assertTrue("Answer 4 must mention Goa Trip", ans4.contains("Goa Trip"))

        // 5. Balance
        val ans5 = NaturalLanguageAssistant.answerQuery(
            query = "What is my current balance?",
            totals = totals,
            categoryBreakdown = catBreakdown,
            budgetResults = budgetResults,
            goalResults = goalResults,
            monthlyTrends = trends,
            transactions = transactions
        )
        assertTrue("Answer 5 must contain balance ₹36,232", ans5.contains("36232"))

        // 6. Subscriptions / recurring
        val ans6 = NaturalLanguageAssistant.answerQuery(
            query = "What subscriptions do I have?",
            totals = totals,
            categoryBreakdown = catBreakdown,
            budgetResults = budgetResults,
            goalResults = goalResults,
            monthlyTrends = trends,
            transactions = transactions
        )
        assertTrue("Answer 6 must detect Netflix", ans6.contains("Netflix"))
    }
}
