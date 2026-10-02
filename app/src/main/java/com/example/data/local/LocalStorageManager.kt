package com.example.data.local

import android.content.Context
import com.example.model.BudgetItem
import com.example.model.CategoryItem
import com.example.model.GoalItem
import com.example.model.TransactionItem
import com.example.model.UserProfile
import java.io.File

data class PersistedSpendWiseData(
    val userProfile: UserProfile,
    val transactions: List<TransactionItem>,
    val categories: List<CategoryItem>,
    val budgets: List<BudgetItem>,
    val goals: List<GoalItem>,
    val settings: SettingsData
)

class LocalStorageManager(val baseDir: File) {

    constructor(context: Context) : this(File(context.filesDir, "spendwise"))

    val userFile = File(baseDir, "user.json")
    val transactionsFile = File(baseDir, "transactions.json")
    val categoriesFile = File(baseDir, "categories.json")
    val budgetsFile = File(baseDir, "budgets.json")
    val goalsFile = File(baseDir, "goals.json")
    val goalContributionsFile = File(baseDir, "goal_contributions.json")
    val settingsFile = File(baseDir, "settings.json")

    val userRepository: UserLocalRepository = JsonUserLocalRepository(userFile)
    val transactionRepository: TransactionLocalRepository = JsonTransactionLocalRepository(transactionsFile)
    val categoryRepository: CategoryLocalRepository = JsonCategoryLocalRepository(categoriesFile)
    val budgetRepository: BudgetLocalRepository = JsonBudgetLocalRepository(budgetsFile)
    val goalRepository: GoalLocalRepository = JsonGoalLocalRepository(goalsFile, goalContributionsFile)
    val settingsRepository: SettingsLocalRepository = JsonSettingsLocalRepository(settingsFile)

    init {
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }
    }

    fun isInitialized(): Boolean {
        return userFile.exists() && transactionsFile.exists()
    }

    @Synchronized
    fun initializeDemoData(
        demoUser: UserProfile,
        demoTransactions: List<TransactionItem>,
        demoCategories: List<CategoryItem>,
        demoBudgets: List<BudgetItem>,
        demoGoals: List<GoalItem>,
        demoSettings: SettingsData
    ) {
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }
        userRepository.saveUser(demoUser)
        transactionRepository.saveTransactions(demoTransactions)
        categoryRepository.saveCategories(demoCategories)
        budgetRepository.saveBudgets(demoBudgets)
        goalRepository.saveGoals(demoGoals)
        settingsRepository.saveSettings(demoSettings)
    }

    @Synchronized
    fun loadAll(): PersistedSpendWiseData {
        val user = userRepository.loadUser() ?: UserProfile()
        val txs = transactionRepository.loadTransactions() ?: emptyList()
        val cats = categoryRepository.loadCategories() ?: emptyList()
        val bgs = budgetRepository.loadBudgets() ?: emptyList()
        val gls = goalRepository.loadGoals() ?: emptyList()
        val sets = settingsRepository.loadSettings() ?: SettingsData()

        return PersistedSpendWiseData(
            userProfile = user,
            transactions = txs,
            categories = cats,
            budgets = bgs,
            goals = gls,
            settings = sets
        )
    }

    @Synchronized
    fun saveTransactions(transactions: List<TransactionItem>) {
        transactionRepository.saveTransactions(transactions)
    }

    @Synchronized
    fun saveBudgets(budgets: List<BudgetItem>) {
        budgetRepository.saveBudgets(budgets)
    }

    @Synchronized
    fun saveGoals(goals: List<GoalItem>) {
        goalRepository.saveGoals(goals)
    }

    @Synchronized
    fun saveCategories(categories: List<CategoryItem>) {
        categoryRepository.saveCategories(categories)
    }

    @Synchronized
    fun saveUserProfile(user: UserProfile) {
        userRepository.saveUser(user)
    }

    @Synchronized
    fun saveSettings(settings: SettingsData) {
        settingsRepository.saveSettings(settings)
    }

    @Synchronized
    fun clearAll() {
        if (baseDir.exists()) {
            baseDir.listFiles()?.forEach { it.delete() }
        }
    }
}
