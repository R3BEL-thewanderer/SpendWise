package com.example.domain.ai.model

enum class InsightType {
    SPENDING,
    BUDGET,
    GOAL,
    SUMMARY,
    ANOMALY
}

enum class InsightSeverity {
    INFO,
    WARNING,
    SUCCESS
}

data class SmartInsight(
    val id: String,
    val title: String,
    val description: String,
    val type: InsightType,
    val severity: InsightSeverity = InsightSeverity.INFO,
    val iconType: String = "sparkles",
    val actionText: String? = null
)

data class MonthlyAiSummary(
    val monthLabel: String,
    val income: Double,
    val expenses: Double,
    val savings: Double,
    val savingsRate: Double,
    val topCategory: String?,
    val topCategoryAmount: Double,
    val budgetUsagePct: Double,
    val aiExplanation: String
)

data class AssistantMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Now",
    val suggestedActions: List<String> = emptyList()
)
