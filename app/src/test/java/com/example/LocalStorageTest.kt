package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.LocalStorageManager
import com.example.data.local.SettingsData
import com.example.data.local.StorageJsonHelper
import com.example.model.CategoryAllocation
import com.example.model.TransactionType
import com.example.state.SpendWiseViewModel
import com.example.state.ThemeMode
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocalStorageTest {

    private lateinit var testStorageDir: File
    private lateinit var storageManager: LocalStorageManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        testStorageDir = File(context.filesDir, "test_spendwise_${System.currentTimeMillis()}")
        if (testStorageDir.exists()) {
            testStorageDir.deleteRecursively()
        }
        testStorageDir.mkdirs()
        storageManager = LocalStorageManager(testStorageDir)
    }

    @After
    fun tearDown() {
        if (testStorageDir.exists()) {
            testStorageDir.deleteRecursively()
        }
    }

    @Test
    fun test1_firstLaunchInitializesDemoData() {
        assertFalse("Storage must not be initialized before first launch", storageManager.isInitialized())

        val vm = SpendWiseViewModel(storageManager)
        vm.syncPersistenceForTesting = true

        assertTrue("Storage must be initialized after ViewModel starts", storageManager.isInitialized())
        assertTrue("user.json must exist", storageManager.userFile.exists())
        assertTrue("transactions.json must exist", storageManager.transactionsFile.exists())
        assertTrue("categories.json must exist", storageManager.categoriesFile.exists())
        assertTrue("budgets.json must exist", storageManager.budgetsFile.exists())
        assertTrue("goals.json must exist", storageManager.goalsFile.exists())
        assertTrue("goal_contributions.json must exist", storageManager.goalContributionsFile.exists())
        assertTrue("settings.json must exist", storageManager.settingsFile.exists())

        // Verify initial demo data
        val persisted = storageManager.loadAll()
        assertEquals("Ashish Singh", persisted.userProfile.name)
        assertTrue("Must contain demo transactions", persisted.transactions.size >= 8)
        assertTrue("Must contain demo categories", persisted.categories.size >= 8)
        assertTrue("Must contain demo budgets", persisted.budgets.isNotEmpty())
        assertTrue("Must contain demo goals", persisted.goals.size >= 4)
    }

    @Test
    fun test2_demoDataPersistsAcrossRestarts() {
        // First launch
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true
        val txCount1 = vm1.transactions.size
        val goalCount1 = vm1.goals.size

        // Simulated app restart (new ViewModel reading same storage)
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        assertEquals(txCount1, vm2.transactions.size)
        assertEquals(goalCount1, vm2.goals.size)
        assertEquals("Starbucks", vm2.transactions.first().title)
    }

    @Test
    fun test3_addTransactionPersistsAfterRestart() {
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true
        val initialCount = vm1.transactions.size

        vm1.addExpense(
            amount = 2499.0,
            title = "Amazon",
            category = "Shopping",
            date = "Today",
            paymentMethod = "HDFC Credit Card",
            tags = listOf("Electronics"),
            notes = "Wireless earbuds"
        )
        assertEquals(initialCount + 1, vm1.transactions.size)

        // Simulated restart
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        assertEquals(initialCount + 1, vm2.transactions.size)
        val addedTx = vm2.transactions.first { it.title == "Amazon" }
        assertEquals(2499.0, addedTx.amount, 0.01)
        assertEquals(TransactionType.EXPENSE, addedTx.type)
        assertEquals("Shopping", addedTx.category)
    }

    @Test
    fun test4_editTransactionPersistsAfterRestart() {
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true

        val originalTx = vm1.transactions.first()
        val updatedTx = originalTx.copy(title = "Starbucks Reserve", amount = 450.0)
        vm1.updateTransaction(updatedTx)

        // Simulated restart
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        val reloadedTx = vm2.transactions.first { it.id == originalTx.id }
        assertEquals("Starbucks Reserve", reloadedTx.title)
        assertEquals(450.0, reloadedTx.amount, 0.01)
    }

    @Test
    fun test5_deleteTransactionPersistsAfterRestart() {
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true
        val initialCount = vm1.transactions.size

        val txToDelete = vm1.transactions.first()
        val deletedId = txToDelete.id
        vm1.deleteTransaction(txToDelete)

        assertEquals(initialCount - 1, vm1.transactions.size)

        // Simulated restart
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        assertEquals(initialCount - 1, vm2.transactions.size)
        assertFalse(vm2.transactions.any { it.id == deletedId })
    }

    @Test
    fun test6_budgetPersistenceWorksAfterRestart() {
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true
        val initialBudgetCount = vm1.budgets.size

        vm1.createBudget(
            name = "April Spring Budget",
            totalLimit = 35000.0,
            month = "April 2026",
            allocations = listOf(
                CategoryAllocation("Shopping", 30, 10500.0),
                CategoryAllocation("Food & Drinks", 40, 14000.0)
            )
        )
        assertEquals(initialBudgetCount + 1, vm1.budgets.size)

        // Simulated restart
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        assertEquals(initialBudgetCount + 1, vm2.budgets.size)
        val newBudget = vm2.budgets.first { it.name == "April Spring Budget" }
        assertEquals(35000.0, newBudget.totalLimit, 0.01)
        assertEquals(2, newBudget.allocations.size)
    }

    @Test
    fun test7_goalPersistenceAndAddMoneyGulakSurvivesRestart() {
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true

        val targetGoal = vm1.goals.first()
        val initialSavings = targetGoal.currentSavings
        val addAmount = 7500.0

        vm1.addMoneyToGoal(targetGoal.id, addAmount)

        // Simulated restart
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        val reloadedGoal = vm2.goals.first { it.id == targetGoal.id }
        assertEquals(initialSavings + addAmount, reloadedGoal.currentSavings, 0.01)
        assertTrue(reloadedGoal.contributions.any { it.amount == addAmount })
    }

    @Test
    fun test8_settingsAndThemePersistenceSurvivesRestart() {
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true

        vm1.themeMode = ThemeMode.DARK
        vm1.isBalanceVisible = false

        // Simulated restart
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        assertEquals(ThemeMode.DARK, vm2.themeMode)
        assertFalse(vm2.isBalanceVisible)
    }

    @Test
    fun test9_missingFilesHandledGracefully() {
        // Initialize once
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true

        // Delete transactions.json
        storageManager.transactionsFile.delete()
        assertFalse(storageManager.transactionsFile.exists())

        // Reopen app - should not crash, loads empty or safe default
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        assertNotNull(vm2.transactions)
        assertNotNull(vm2.userProfile)
        assertEquals("Ashish Singh", vm2.userProfile.name)
    }

    @Test
    fun test10_corruptedJsonHandledGracefully() {
        val vm1 = SpendWiseViewModel(storageManager)
        vm1.syncPersistenceForTesting = true

        // Corrupt transactions.json with garbage text
        storageManager.transactionsFile.writeText("{ corrupted: invalid json syntax !!!")

        // Reopen app - should recover gracefully with safe defaults without throwing exception
        val vm2 = SpendWiseViewModel(storageManager)
        vm2.syncPersistenceForTesting = true

        assertNotNull(vm2.transactions)
        assertTrue("Transactions should gracefully handle corrupt file", vm2.transactions.isEmpty())
        assertNotNull(vm2.userProfile)
    }

    @Test
    fun test11_deleteAccountClearsLocalStorage() {
        val vm = SpendWiseViewModel(storageManager)
        vm.syncPersistenceForTesting = true

        assertTrue(storageManager.isInitialized())
        vm.deleteAccount()

        assertFalse("Storage must no longer have files after delete account", storageManager.isInitialized())
        assertEquals(0, vm.transactions.size)
        assertEquals(0, vm.goals.size)
        assertEquals(0, vm.budgets.size)
    }
}
