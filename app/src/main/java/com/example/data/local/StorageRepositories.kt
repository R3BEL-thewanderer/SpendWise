package com.example.data.local

import android.util.Log
import com.example.model.BudgetItem
import com.example.model.CategoryItem
import com.example.model.GoalItem
import com.example.model.TransactionItem
import com.example.model.UserProfile
import java.io.File
import java.io.FileOutputStream

private const val TAG = "SpendWiseStorage"

object SafeFileIo {
    fun writeSafe(targetFile: File, content: String) {
        val parent = targetFile.parentFile ?: return
        if (!parent.exists()) {
            parent.mkdirs()
        }
        val tempFile = File.createTempFile("tmp_${targetFile.nameWithoutExtension}_", ".tmp", parent)
        try {
            FileOutputStream(tempFile).use { fos ->
                fos.write(content.toByteArray(Charsets.UTF_8))
                fos.flush()
                try {
                    fos.fd.sync()
                } catch (_: Exception) {
                    // Sync may not be supported on all virtual file systems, ignore safely
                }
            }
            if (targetFile.exists()) {
                targetFile.delete()
            }
            if (!tempFile.renameTo(targetFile)) {
                tempFile.copyTo(targetFile, overwrite = true)
                tempFile.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed safe write to ${targetFile.name}: ${e.message}")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            throw e
        }
    }

    fun readSafe(file: File): String? {
        if (!file.exists() || !file.canRead()) return null
        return try {
            val text = file.readText(Charsets.UTF_8).trim()
            if (text.isEmpty()) null else text
        } catch (e: Exception) {
            Log.e(TAG, "Failed safe read from ${file.name}: ${e.message}")
            null
        }
    }
}

interface UserLocalRepository {
    fun loadUser(): UserProfile?
    fun saveUser(user: UserProfile)
    fun exists(): Boolean
}

interface TransactionLocalRepository {
    fun loadTransactions(): List<TransactionItem>?
    fun saveTransactions(transactions: List<TransactionItem>)
    fun exists(): Boolean
}

interface CategoryLocalRepository {
    fun loadCategories(): List<CategoryItem>?
    fun saveCategories(categories: List<CategoryItem>)
    fun exists(): Boolean
}

interface BudgetLocalRepository {
    fun loadBudgets(): List<BudgetItem>?
    fun saveBudgets(budgets: List<BudgetItem>)
    fun exists(): Boolean
}

interface GoalLocalRepository {
    fun loadGoals(): List<GoalItem>?
    fun saveGoals(goals: List<GoalItem>)
    fun exists(): Boolean
}

interface SettingsLocalRepository {
    fun loadSettings(): SettingsData?
    fun saveSettings(settings: SettingsData)
    fun exists(): Boolean
}

class JsonUserLocalRepository(private val file: File) : UserLocalRepository {
    override fun exists(): Boolean = file.exists()

    override fun loadUser(): UserProfile? {
        val content = SafeFileIo.readSafe(file) ?: return null
        return try {
            StorageJsonHelper.jsonToUserProfile(content)
        } catch (e: Exception) {
            Log.w(TAG, "Error deserializing user.json, using default: ${e.message}")
            null
        }
    }

    override fun saveUser(user: UserProfile) {
        val json = StorageJsonHelper.userProfileToJson(user)
        SafeFileIo.writeSafe(file, json)
    }
}

class JsonTransactionLocalRepository(private val file: File) : TransactionLocalRepository {
    override fun exists(): Boolean = file.exists()

    override fun loadTransactions(): List<TransactionItem>? {
        val content = SafeFileIo.readSafe(file) ?: return null
        return try {
            StorageJsonHelper.jsonToTransactions(content)
        } catch (e: Exception) {
            Log.w(TAG, "Error deserializing transactions.json: ${e.message}")
            null
        }
    }

    override fun saveTransactions(transactions: List<TransactionItem>) {
        val json = StorageJsonHelper.transactionsToJson(transactions)
        SafeFileIo.writeSafe(file, json)
    }
}

class JsonCategoryLocalRepository(private val file: File) : CategoryLocalRepository {
    override fun exists(): Boolean = file.exists()

    override fun loadCategories(): List<CategoryItem>? {
        val content = SafeFileIo.readSafe(file) ?: return null
        return try {
            StorageJsonHelper.jsonToCategories(content)
        } catch (e: Exception) {
            Log.w(TAG, "Error deserializing categories.json: ${e.message}")
            null
        }
    }

    override fun saveCategories(categories: List<CategoryItem>) {
        val json = StorageJsonHelper.categoriesToJson(categories)
        SafeFileIo.writeSafe(file, json)
    }
}

class JsonBudgetLocalRepository(private val file: File) : BudgetLocalRepository {
    override fun exists(): Boolean = file.exists()

    override fun loadBudgets(): List<BudgetItem>? {
        val content = SafeFileIo.readSafe(file) ?: return null
        return try {
            StorageJsonHelper.jsonToBudgets(content)
        } catch (e: Exception) {
            Log.w(TAG, "Error deserializing budgets.json: ${e.message}")
            null
        }
    }

    override fun saveBudgets(budgets: List<BudgetItem>) {
        val json = StorageJsonHelper.budgetsToJson(budgets)
        SafeFileIo.writeSafe(file, json)
    }
}

class JsonGoalLocalRepository(
    private val goalsFile: File,
    private val contributionsFile: File
) : GoalLocalRepository {
    override fun exists(): Boolean = goalsFile.exists()

    override fun loadGoals(): List<GoalItem>? {
        val goalsContent = SafeFileIo.readSafe(goalsFile) ?: return null
        val contribsContent = SafeFileIo.readSafe(contributionsFile)
        return try {
            StorageJsonHelper.jsonToGoals(goalsContent, contribsContent)
        } catch (e: Exception) {
            Log.w(TAG, "Error deserializing goals.json: ${e.message}")
            null
        }
    }

    override fun saveGoals(goals: List<GoalItem>) {
        val goalsJson = StorageJsonHelper.goalsToJson(goals)
        SafeFileIo.writeSafe(goalsFile, goalsJson)

        val contribsJson = StorageJsonHelper.goalContributionsToJson(goals)
        SafeFileIo.writeSafe(contributionsFile, contribsJson)
    }
}

class JsonSettingsLocalRepository(private val file: File) : SettingsLocalRepository {
    override fun exists(): Boolean = file.exists()

    override fun loadSettings(): SettingsData? {
        val content = SafeFileIo.readSafe(file) ?: return null
        return try {
            StorageJsonHelper.jsonToSettings(content)
        } catch (e: Exception) {
            Log.w(TAG, "Error deserializing settings.json: ${e.message}")
            null
        }
    }

    override fun saveSettings(settings: SettingsData) {
        val json = StorageJsonHelper.settingsToJson(settings)
        SafeFileIo.writeSafe(file, json)
    }
}
