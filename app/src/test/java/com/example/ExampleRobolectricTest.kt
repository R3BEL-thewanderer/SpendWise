package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SpendWise", appName)
  }

  @Test
  fun `spendwise viewmodel initialization and navigation`() {
    val viewModel = SpendWiseViewModel()
    assertEquals(AppScreen.WELCOME, viewModel.currentScreen)
    assertTrue(viewModel.transactions.isNotEmpty())
    assertTrue(viewModel.goals.isNotEmpty())
    assertTrue(viewModel.budgets.isNotEmpty())

    // Test Navigation
    viewModel.navigateTo(AppScreen.SIGN_UP)
    assertEquals(AppScreen.SIGN_UP, viewModel.currentScreen)

    viewModel.navigateTo(AppScreen.HOME)
    assertEquals(AppScreen.HOME, viewModel.currentScreen)

    viewModel.navigateBack()
    assertEquals(AppScreen.SIGN_UP, viewModel.currentScreen)
  }

  @Test
  fun `add expense updates transactions and balance`() {
    val viewModel = SpendWiseViewModel()
    val initialTxCount = viewModel.transactions.size
    val initialExpenses = viewModel.monthlyExpenses

    viewModel.addExpense(
      amount = 500.0,
      title = "Coffee Roasters",
      category = "Food & Dining",
      date = "Today",
      paymentMethod = "UPI",
      tags = listOf("Coffee"),
      notes = "Test note"
    )

    assertEquals(initialTxCount + 1, viewModel.transactions.size)
    assertEquals(initialExpenses + 500.0, viewModel.monthlyExpenses, 0.01)
    assertEquals("Coffee Roasters", viewModel.transactions.first().title)
  }

  @Test
  fun `add money to goal updates digital gulak savings`() {
    val viewModel = SpendWiseViewModel()
    val goal = viewModel.goals.first()
    val initialSavings = goal.currentSavings

    viewModel.addMoneyToGoal(goal.id, 5000.0)

    val updatedGoal = viewModel.goals.first { it.id == goal.id }
    assertEquals(initialSavings + 5000.0, updatedGoal.currentSavings, 0.01)
    assertTrue(updatedGoal.contributions.isNotEmpty())
    assertEquals(5000.0, updatedGoal.contributions.first().amount, 0.01)
  }
}
