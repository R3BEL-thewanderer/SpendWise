package com.example.domain.calculator

import com.example.domain.model.GoalCalculationResult
import com.example.model.GoalItem
import kotlin.math.max

object GoalCalculator {

    /**
     * Determines actual amount saved for a goal.
     * Where contributions are present, the sum of contributions is the source of truth,
     * otherwise falls back to currentSavings.
     */
    fun calculateSavedAmount(goal: GoalItem): Double {
        val amount = if (goal.currentSavings > 0.0) {
            goal.currentSavings
        } else {
            goal.contributions.sumOf { it.amount }
        }
        return FinanceCalculator.roundMoney(amount)
    }

    /**
     * Calculates Digital Gulak fill ratio clamped between 0.0f and 1.0f.
     * Formula: Amount Saved / Target Amount.
     */
    fun calculateGulakFillRatio(savedAmount: Double, targetAmount: Double): Float {
        if (targetAmount <= 0.0) return 0.0f
        return (savedAmount / targetAmount).toFloat().coerceIn(0.0f, 1.0f)
    }

    /**
     * Calculates progress percentage.
     * Formula: (Amount Saved / Target Amount) * 100.
     */
    fun calculateProgressPercent(savedAmount: Double, targetAmount: Double): Double {
        if (targetAmount <= 0.0) return 0.0
        val pct = (savedAmount / targetAmount) * 100.0
        return FinanceCalculator.roundMoney(pct)
    }

    /**
     * Calculates remaining goal amount.
     * Formula: max(0.0, Target Amount - Amount Saved).
     */
    fun calculateRemainingAmount(savedAmount: Double, targetAmount: Double): Double {
        return FinanceCalculator.roundMoney(max(0.0, targetAmount - savedAmount))
    }

    /**
     * Checks if goal is completed.
     * Formula: Amount Saved >= Target Amount.
     */
    fun isGoalCompleted(savedAmount: Double, targetAmount: Double): Boolean {
        if (targetAmount <= 0.0) return false
        return savedAmount >= targetAmount
    }

    /**
     * Computes complete goal calculation result for a GoalItem.
     */
    fun calculateGoal(goal: GoalItem): GoalCalculationResult {
        val saved = calculateSavedAmount(goal)
        val remaining = calculateRemainingAmount(saved, goal.targetAmount)
        val progress = calculateProgressPercent(saved, goal.targetAmount)
        val completed = isGoalCompleted(saved, goal.targetAmount)
        val gulakRatio = calculateGulakFillRatio(saved, goal.targetAmount)

        return GoalCalculationResult(
            goalId = goal.id,
            name = goal.name,
            targetAmount = goal.targetAmount,
            savedAmount = saved,
            remainingAmount = remaining,
            progressPercent = progress,
            isCompleted = completed,
            gulakFillRatio = gulakRatio,
            contributionCount = goal.contributions.size
        )
    }

    /**
     * Computes results for all goals.
     */
    fun calculateAllGoals(goals: List<GoalItem>): List<GoalCalculationResult> {
        return goals.map { calculateGoal(it) }
    }

    /**
     * Computes overall goal savings progress across all goals.
     */
    fun calculateOverallProgress(goals: List<GoalItem>): Double {
        val totalTarget = goals.sumOf { it.targetAmount }
        if (totalTarget <= 0.0) return 0.0
        val totalSaved = goals.sumOf { calculateSavedAmount(it) }
        return FinanceCalculator.roundMoney((totalSaved / totalTarget) * 100.0)
    }
}
