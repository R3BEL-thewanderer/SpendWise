package com.example.data.local

import com.example.model.BudgetItem
import com.example.model.CategoryAllocation
import com.example.model.CategoryItem
import com.example.model.GoalContribution
import com.example.model.GoalItem
import com.example.model.TransactionItem
import com.example.model.TransactionType
import com.example.model.UserProfile
import org.json.JSONArray
import org.json.JSONObject

data class SettingsData(
    val themeMode: String = "LIGHT",
    val isBalanceVisible: Boolean = true,
    val totalBalance: Double = 48250.0,
    val monthlyIncome: Double = 45000.0,
    val monthlyExpenses: Double = 24580.0
)

data class FlatGoalContribution(
    val id: String,
    val goalId: String,
    val amount: Double,
    val date: String,
    val note: String
)

object StorageJsonHelper {

    // --- User Profile ---
    fun userProfileToJson(profile: UserProfile): String {
        val obj = JSONObject()
        obj.put("name", profile.name)
        obj.put("email", profile.email)
        obj.put("currency", profile.currency)
        obj.put("monthlyIncome", profile.monthlyIncome)
        obj.put("monthlyBudget", profile.monthlyBudget)
        val prefArr = JSONArray()
        profile.preferredCategories.forEach { prefArr.put(it) }
        obj.put("preferredCategories", prefArr)
        obj.put("memberSince", profile.memberSince)
        obj.put("accountType", profile.accountType)
        return obj.toString(2)
    }

    fun jsonToUserProfile(jsonString: String): UserProfile {
        val obj = JSONObject(jsonString)
        val prefList = mutableListOf<String>()
        val prefArr = obj.optJSONArray("preferredCategories")
        if (prefArr != null) {
            for (i in 0 until prefArr.length()) {
                prefList.add(prefArr.optString(i))
            }
        }
        return UserProfile(
            name = obj.optString("name", "Ashish Singh"),
            email = obj.optString("email", "ashish.singh@example.com"),
            currency = obj.optString("currency", "INR ₹"),
            monthlyIncome = obj.optDouble("monthlyIncome", 45000.0),
            monthlyBudget = obj.optDouble("monthlyBudget", 30000.0),
            preferredCategories = if (prefList.isNotEmpty()) prefList else listOf("Food & Dining", "Shopping", "Transport", "Bills & Utilities"),
            memberSince = obj.optString("memberSince", "12 Feb 2025"),
            accountType = obj.optString("accountType", "Free Plan")
        )
    }

    // --- Transactions ---
    fun transactionsToJson(list: List<TransactionItem>): String {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("subtitle", item.subtitle)
            obj.put("amount", item.amount)
            obj.put("type", item.type.name)
            obj.put("category", item.category)
            obj.put("date", item.date)
            obj.put("time", item.time)
            obj.put("paymentMethod", item.paymentMethod)
            val tagsArr = JSONArray()
            item.tags.forEach { tagsArr.put(it) }
            obj.put("tags", tagsArr)
            obj.put("notes", item.notes)
            obj.put("iconType", item.iconType)
            obj.put("colorHex", item.colorHex)
            array.put(obj)
        }
        return array.toString(2)
    }

    fun jsonToTransactions(jsonString: String): List<TransactionItem> {
        val array = JSONArray(jsonString)
        val result = mutableListOf<TransactionItem>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val tags = mutableListOf<String>()
            val tagsArr = obj.optJSONArray("tags")
            if (tagsArr != null) {
                for (j in 0 until tagsArr.length()) {
                    tags.add(tagsArr.optString(j))
                }
            }
            val typeStr = obj.optString("type", "EXPENSE")
            val type = try {
                TransactionType.valueOf(typeStr)
            } catch (_: Exception) {
                TransactionType.EXPENSE
            }
            result.add(
                TransactionItem(
                    id = obj.optString("id", "tx-${System.currentTimeMillis()}-$i"),
                    title = obj.optString("title", "Untitled"),
                    subtitle = obj.optString("subtitle", ""),
                    amount = obj.optDouble("amount", 0.0),
                    type = type,
                    category = obj.optString("category", "General"),
                    date = obj.optString("date", "Today"),
                    time = obj.optString("time", "Now"),
                    paymentMethod = obj.optString("paymentMethod", "UPI"),
                    tags = if (tags.isNotEmpty()) tags else listOf("Essentials"),
                    notes = obj.optString("notes", ""),
                    iconType = obj.optString("iconType", "food"),
                    colorHex = obj.optLong("colorHex", 0xFFEF9C8DL)
                )
            )
        }
        return result
    }

    // --- Categories ---
    fun categoriesToJson(list: List<CategoryItem>): String {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("iconType", item.iconType)
            obj.put("colorHex", item.colorHex)
            obj.put("spentAmount", item.spentAmount)
            obj.put("transactionCount", item.transactionCount)
            array.put(obj)
        }
        return array.toString(2)
    }

    fun jsonToCategories(jsonString: String): List<CategoryItem> {
        val array = JSONArray(jsonString)
        val result = mutableListOf<CategoryItem>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            result.add(
                CategoryItem(
                    id = obj.optString("id", "cat-$i"),
                    name = obj.optString("name", "Category"),
                    iconType = obj.optString("iconType", "food"),
                    colorHex = obj.optLong("colorHex", 0xFF9CC9FFL),
                    spentAmount = obj.optDouble("spentAmount", 0.0),
                    transactionCount = obj.optInt("transactionCount", 0)
                )
            )
        }
        return result
    }

    // --- Budgets ---
    fun budgetsToJson(list: List<BudgetItem>): String {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("month", item.month)
            obj.put("totalLimit", item.totalLimit)
            obj.put("spent", item.spent)
            obj.put("alertThresholdPercent", item.alertThresholdPercent)

            val allocArr = JSONArray()
            for (alloc in item.allocations) {
                val aObj = JSONObject()
                aObj.put("categoryName", alloc.categoryName)
                aObj.put("percentage", alloc.percentage)
                aObj.put("amount", alloc.amount)
                aObj.put("colorHex", alloc.colorHex)
                aObj.put("iconType", alloc.iconType)
                allocArr.put(aObj)
            }
            obj.put("allocations", allocArr)
            array.put(obj)
        }
        return array.toString(2)
    }

    fun jsonToBudgets(jsonString: String): List<BudgetItem> {
        val array = JSONArray(jsonString)
        val result = mutableListOf<BudgetItem>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val allocList = mutableListOf<CategoryAllocation>()
            val allocArr = obj.optJSONArray("allocations")
            if (allocArr != null) {
                for (j in 0 until allocArr.length()) {
                    val aObj = allocArr.optJSONObject(j) ?: continue
                    allocList.add(
                        CategoryAllocation(
                            categoryName = aObj.optString("categoryName", "General"),
                            percentage = aObj.optInt("percentage", 0),
                            amount = aObj.optDouble("amount", 0.0),
                            colorHex = aObj.optLong("colorHex", 0xFF9CC9FFL),
                            iconType = aObj.optString("iconType", "food")
                        )
                    )
                }
            }
            result.add(
                BudgetItem(
                    id = obj.optString("id", "budget-$i"),
                    name = obj.optString("name", "Monthly Budget"),
                    month = obj.optString("month", "This Month"),
                    totalLimit = obj.optDouble("totalLimit", 0.0),
                    spent = obj.optDouble("spent", 0.0),
                    allocations = allocList,
                    alertThresholdPercent = obj.optInt("alertThresholdPercent", 90)
                )
            )
        }
        return result
    }

    // --- Goals & Goal Contributions ---
    fun goalsToJson(list: List<GoalItem>): String {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("targetAmount", item.targetAmount)
            obj.put("currentSavings", item.currentSavings)
            obj.put("targetDate", item.targetDate)
            obj.put("category", item.category)
            obj.put("iconType", item.iconType)
            obj.put("colorHex", item.colorHex)
            obj.put("description", item.description)

            val contribArr = JSONArray()
            for (c in item.contributions) {
                val cObj = JSONObject()
                cObj.put("id", c.id)
                cObj.put("amount", c.amount)
                cObj.put("date", c.date)
                cObj.put("note", c.note)
                contribArr.put(cObj)
            }
            obj.put("contributions", contribArr)
            array.put(obj)
        }
        return array.toString(2)
    }

    fun goalContributionsToJson(goals: List<GoalItem>): String {
        val array = JSONArray()
        for (goal in goals) {
            for (c in goal.contributions) {
                val cObj = JSONObject()
                cObj.put("id", c.id)
                cObj.put("goalId", goal.id)
                cObj.put("amount", c.amount)
                cObj.put("date", c.date)
                cObj.put("note", c.note)
                array.put(cObj)
            }
        }
        return array.toString(2)
    }

    fun jsonToGoals(goalsJson: String, contributionsJson: String? = null): List<GoalItem> {
        val array = JSONArray(goalsJson)
        val result = mutableListOf<GoalItem>()

        // Optional external contributions mapping by goalId
        val externalContributionsMap = mutableMapOf<String, MutableList<GoalContribution>>()
        if (!contributionsJson.isNullOrBlank()) {
            try {
                val contribArray = JSONArray(contributionsJson)
                for (k in 0 until contribArray.length()) {
                    val cObj = contribArray.optJSONObject(k) ?: continue
                    val goalId = cObj.optString("goalId", "")
                    if (goalId.isNotEmpty()) {
                        val contrib = GoalContribution(
                            id = cObj.optString("id", "c-$k"),
                            amount = cObj.optDouble("amount", 0.0),
                            date = cObj.optString("date", "Today"),
                            note = cObj.optString("note", "Added Money")
                        )
                        externalContributionsMap.getOrPut(goalId) { mutableListOf() }.add(contrib)
                    }
                }
            } catch (_: Exception) {
                // If contributionsJson fails to parse, fallback to inline contributions
            }
        }

        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val goalId = obj.optString("id", "goal-$i")
            val contribList = mutableListOf<GoalContribution>()

            // Inline contributions first
            val contribArr = obj.optJSONArray("contributions")
            if (contribArr != null && contribArr.length() > 0) {
                for (j in 0 until contribArr.length()) {
                    val cObj = contribArr.optJSONObject(j) ?: continue
                    contribList.add(
                        GoalContribution(
                            id = cObj.optString("id", "c-$j"),
                            amount = cObj.optDouble("amount", 0.0),
                            date = cObj.optString("date", "Today"),
                            note = cObj.optString("note", "Added Money")
                        )
                    )
                }
            } else if (externalContributionsMap.containsKey(goalId)) {
                contribList.addAll(externalContributionsMap[goalId] ?: emptyList())
            }

            result.add(
                GoalItem(
                    id = goalId,
                    name = obj.optString("name", "Savings Goal"),
                    targetAmount = obj.optDouble("targetAmount", 0.0),
                    currentSavings = obj.optDouble("currentSavings", 0.0),
                    targetDate = obj.optString("targetDate", "Soon"),
                    category = obj.optString("category", "General"),
                    iconType = obj.optString("iconType", "laptop"),
                    colorHex = obj.optLong("colorHex", 0xFF9CC9FFL),
                    description = obj.optString("description", ""),
                    contributions = contribList
                )
            )
        }
        return result
    }

    // --- Settings ---
    fun settingsToJson(settings: SettingsData): String {
        val obj = JSONObject()
        obj.put("themeMode", settings.themeMode)
        obj.put("isBalanceVisible", settings.isBalanceVisible)
        obj.put("totalBalance", settings.totalBalance)
        obj.put("monthlyIncome", settings.monthlyIncome)
        obj.put("monthlyExpenses", settings.monthlyExpenses)
        return obj.toString(2)
    }

    fun jsonToSettings(jsonString: String): SettingsData {
        val obj = JSONObject(jsonString)
        return SettingsData(
            themeMode = obj.optString("themeMode", "LIGHT"),
            isBalanceVisible = obj.optBoolean("isBalanceVisible", true),
            totalBalance = obj.optDouble("totalBalance", 48250.0),
            monthlyIncome = obj.optDouble("monthlyIncome", 45000.0),
            monthlyExpenses = obj.optDouble("monthlyExpenses", 24580.0)
        )
    }
}
