package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.TransactionType
import com.example.state.ActiveModal
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import com.example.state.ThemeMode
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.SuccessToast
import com.example.ui.screens.AddCategoryScreen
import com.example.ui.screens.AddExpenseScreen
import com.example.ui.screens.AddIncomeScreen
import com.example.ui.screens.AddMoneyToGoalScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AppearanceScreen
import com.example.ui.screens.BudgetDetailScreen
import com.example.ui.screens.BudgetsOverviewScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CreateBudgetScreen
import com.example.ui.screens.CreateGoalScreen
import com.example.ui.screens.EditBudgetScreen
import com.example.ui.screens.EditCategoryScreen
import com.example.ui.screens.EditExpenseScreen
import com.example.ui.screens.EditIncomeScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.GoalDetailScreen
import com.example.ui.screens.GoalsOverviewScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.ScanReceiptModal
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.TransactionDetailScreen
import com.example.ui.screens.TransactionFilterDialog
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val spendWiseViewModel: SpendWiseViewModel = viewModel()
            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (spendWiseViewModel.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemDark
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                SpendWiseApp(viewModel = spendWiseViewModel)
            }
        }
    }
}

@Composable
fun SpendWiseApp(viewModel: SpendWiseViewModel) {
    // Back navigation handling
    val isRootScreen = viewModel.currentScreen == AppScreen.WELCOME || viewModel.currentScreen == AppScreen.HOME
    BackHandler(enabled = !isRootScreen) {
        viewModel.navigateBack()
    }

    val showBottomNav = viewModel.currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.TRANSACTIONS,
        AppScreen.BUDGETS,
        AppScreen.GOALS,
        AppScreen.ANALYTICS,
        AppScreen.PROFILE
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Screen Transitions
            AnimatedContent(
                targetState = viewModel.currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.WELCOME -> WelcomeScreen(viewModel)
                    AppScreen.SIGN_UP -> SignUpScreen(viewModel)
                    AppScreen.SIGN_IN -> SignInScreen(viewModel)
                    AppScreen.FORGOT_PASSWORD -> ForgotPasswordScreen(viewModel)
                    AppScreen.PROFILE_SETUP -> ProfileSetupScreen(viewModel)
                    AppScreen.HOME -> HomeScreen(viewModel)
                    AppScreen.TRANSACTIONS -> TransactionsScreen(viewModel)
                    AppScreen.TRANSACTION_DETAIL -> TransactionDetailScreen(viewModel)
                    AppScreen.ADD_EXPENSE -> AddExpenseScreen(viewModel)
                    AppScreen.EDIT_EXPENSE -> EditExpenseScreen(viewModel)
                    AppScreen.ADD_INCOME -> AddIncomeScreen(viewModel)
                    AppScreen.EDIT_INCOME -> EditIncomeScreen(viewModel)
                    AppScreen.BUDGETS -> BudgetsOverviewScreen(viewModel)
                    AppScreen.CREATE_BUDGET -> CreateBudgetScreen(viewModel)
                    AppScreen.BUDGET_DETAIL -> BudgetDetailScreen(viewModel)
                    AppScreen.EDIT_BUDGET -> EditBudgetScreen(viewModel)
                    AppScreen.GOALS -> GoalsOverviewScreen(viewModel)
                    AppScreen.CREATE_GOAL -> CreateGoalScreen(viewModel)
                    AppScreen.GOAL_DETAIL -> GoalDetailScreen(viewModel)
                    AppScreen.ADD_MONEY_GOAL -> AddMoneyToGoalScreen(viewModel)
                    AppScreen.CATEGORIES -> CategoriesScreen(viewModel)
                    AppScreen.ADD_CATEGORY -> AddCategoryScreen(viewModel)
                    AppScreen.EDIT_CATEGORY -> EditCategoryScreen(viewModel)
                    AppScreen.ANALYTICS -> AnalyticsScreen(viewModel)
                    AppScreen.PROFILE -> ProfileScreen(viewModel)
                    AppScreen.SETTINGS -> SettingsScreen(viewModel)
                    AppScreen.APPEARANCE -> AppearanceScreen(viewModel)
                }
            }

            // Bottom Navigation Overlay
            if (showBottomNav) {
                AppBottomNavigation(
                    currentScreen = viewModel.currentScreen,
                    onTabSelected = { targetScreen ->
                        viewModel.navigateTo(targetScreen)
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // Feedback Toast Overlay
            SuccessToast(
                message = viewModel.toastMessage,
                onDismiss = { viewModel.dismissToast() },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Modals & Dialogs
            when (viewModel.activeModal) {
                ActiveModal.DELETE_TRANSACTION -> {
                    val tx = viewModel.selectedTransaction
                    ConfirmationDialog(
                        title = "Delete this transaction?",
                        message = "This will permanently remove this transaction from your records. This action cannot be undone.",
                        confirmButtonText = "Delete Transaction",
                        icon = Icons.Default.Delete,
                        isDestructive = true,
                        onConfirm = {
                            if (tx != null) viewModel.deleteTransaction(tx)
                            viewModel.activeModal = ActiveModal.NONE
                            if (viewModel.currentScreen == AppScreen.TRANSACTION_DETAIL ||
                                viewModel.currentScreen == AppScreen.EDIT_EXPENSE
                            ) {
                                viewModel.navigateTo(AppScreen.TRANSACTIONS)
                            }
                        },
                        onDismiss = { viewModel.activeModal = ActiveModal.NONE }
                    )
                }

                ActiveModal.DELETE_INCOME -> {
                    val tx = viewModel.selectedTransaction
                    ConfirmationDialog(
                        title = "Delete this income?",
                        message = "This will permanently remove this income entry from your records. This action cannot be undone.",
                        confirmButtonText = "Delete Income",
                        icon = Icons.Default.Delete,
                        isDestructive = true,
                        onConfirm = {
                            if (tx != null) viewModel.deleteTransaction(tx)
                            viewModel.activeModal = ActiveModal.NONE
                            viewModel.navigateTo(AppScreen.TRANSACTIONS)
                        },
                        onDismiss = { viewModel.activeModal = ActiveModal.NONE }
                    )
                }

                ActiveModal.DELETE_BUDGET -> {
                    val b = viewModel.selectedBudget
                    ConfirmationDialog(
                        title = "Delete this budget?",
                        message = "This will permanently remove your budget configuration, including all category limits and alert settings.",
                        confirmButtonText = "Delete Budget",
                        icon = Icons.Default.Delete,
                        isDestructive = true,
                        onConfirm = {
                            if (b != null) viewModel.deleteBudget(b)
                            viewModel.activeModal = ActiveModal.NONE
                            viewModel.navigateTo(AppScreen.BUDGETS)
                        },
                        onDismiss = { viewModel.activeModal = ActiveModal.NONE }
                    )
                }

                ActiveModal.DELETE_GOAL -> {
                    val g = viewModel.selectedGoal
                    ConfirmationDialog(
                        title = "Delete this goal?",
                        message = "This will remove '${g?.name ?: "Goal"}' and its contribution history from your savings tracking.",
                        confirmButtonText = "Delete Goal",
                        icon = Icons.Default.Delete,
                        isDestructive = true,
                        onConfirm = {
                            if (g != null) viewModel.deleteGoal(g)
                            viewModel.activeModal = ActiveModal.NONE
                            viewModel.navigateTo(AppScreen.GOALS)
                        },
                        onDismiss = { viewModel.activeModal = ActiveModal.NONE }
                    )
                }

                ActiveModal.LOGOUT -> {
                    ConfirmationDialog(
                        title = "Log out of SpendWise?",
                        message = "You'll need to sign in again to access your account and view your financial data.",
                        confirmButtonText = "Log Out",
                        icon = Icons.Default.Logout,
                        isDestructive = true,
                        onConfirm = {
                            viewModel.activeModal = ActiveModal.NONE
                            viewModel.navigateTo(AppScreen.WELCOME)
                            viewModel.showToast("Logged out successfully")
                        },
                        onDismiss = { viewModel.activeModal = ActiveModal.NONE }
                    )
                }

                ActiveModal.TRANSACTION_FILTER -> {
                    TransactionFilterDialog(
                        viewModel = viewModel,
                        onDismiss = { viewModel.activeModal = ActiveModal.NONE }
                    )
                }

                ActiveModal.SCAN_RECEIPT -> {
                    ScanReceiptModal(
                        viewModel = viewModel,
                        onDismiss = { viewModel.activeModal = ActiveModal.NONE }
                    )
                }

                else -> {}
            }
        }
    }
}
