package com.example.domain.validation

import com.example.domain.model.ValidationResult
import com.example.model.TransactionType

object FinancialValidator {

    fun validateTransaction(
        amount: Double,
        type: TransactionType?,
        category: String,
        title: String,
        date: String
    ): ValidationResult {
        if (amount <= 0.0) {
            return ValidationResult.Invalid("amount", "Amount must be greater than zero")
        }
        if (type == null) {
            return ValidationResult.Invalid("type", "Transaction type is required")
        }
        if (title.isBlank()) {
            return ValidationResult.Invalid("title", "Title cannot be empty")
        }
        if (category.isBlank()) {
            return ValidationResult.Invalid("category", "Category cannot be empty")
        }
        if (date.isBlank()) {
            return ValidationResult.Invalid("date", "Date cannot be empty")
        }
        return ValidationResult.Valid
    }

    fun validateBudget(
        name: String,
        totalLimit: Double,
        month: String
    ): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult.Invalid("name", "Budget name cannot be empty")
        }
        if (totalLimit <= 0.0) {
            return ValidationResult.Invalid("totalLimit", "Budget limit must be greater than zero")
        }
        if (month.isBlank()) {
            return ValidationResult.Invalid("month", "Budget month/period cannot be empty")
        }
        return ValidationResult.Valid
    }

    fun validateGoal(
        name: String,
        targetAmount: Double,
        currentSavings: Double = 0.0
    ): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult.Invalid("name", "Goal name cannot be empty")
        }
        if (targetAmount <= 0.0) {
            return ValidationResult.Invalid("targetAmount", "Target amount must be greater than zero")
        }
        if (currentSavings < 0.0) {
            return ValidationResult.Invalid("currentSavings", "Savings amount cannot be negative")
        }
        return ValidationResult.Valid
    }

    fun validateContribution(
        amount: Double,
        goalExists: Boolean
    ): ValidationResult {
        if (!goalExists) {
            return ValidationResult.Invalid("goalId", "Referenced goal does not exist")
        }
        if (amount <= 0.0) {
            return ValidationResult.Invalid("amount", "Contribution amount must be greater than zero")
        }
        return ValidationResult.Valid
    }

    fun validateCategory(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult.Invalid("name", "Category name cannot be empty")
        }
        return ValidationResult.Valid
    }
}
