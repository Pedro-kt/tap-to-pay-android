package com.yumedev.taptopayandroid.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yumedev.taptopayandroid.R
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yumedev.taptopayandroid.domain.model.*
import com.yumedev.taptopayandroid.presentation.ui.screens.*
import com.yumedev.taptopayandroid.presentation.viewmodel.TapToPayViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    onThemeChanged: (String) -> Unit = {}
) {
    // Shared ViewModel across navigation
    val sharedViewModel: TapToPayViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Home.route
    ) {
        composable(route = NavigationRoutes.Home.route) {

            LaunchedEffect(Unit) {
                sharedViewModel.clearStateOnly()
            }

            HomeScreen(
                onGeneratePayment = { amount ->
                    navController.navigate(NavigationRoutes.TapToPay.createRoute(amount))
                },
                innerPadding = innerPadding
            )
        }

        composable(
            route = NavigationRoutes.TapToPay.route,
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: "$0.00"
            TapToPayScreen(
                amount = amount,
                onCancel = {
                    navController.popBackStack()
                },
                onSuccess = { emvCardData ->
                    navController.navigate(
                        NavigationRoutes.Success.createRoute(
                            amount = amount,
                            cardNumber = emvCardData.cardholderData.panLastFour,
                            cardType = emvCardData.cardType.name
                        )
                    ) {
                        // Remove TapToPay from back stack
                        popUpTo(NavigationRoutes.TapToPay.route) {
                            inclusive = true
                        }
                    }
                },
                onError = { errorMessage ->
                    navController.navigate(
                        NavigationRoutes.Error.createRoute(
                            amount = amount,
                            errorMessage = errorMessage
                        )
                    ) {
                        // Remove TapToPay from back stack
                        popUpTo(NavigationRoutes.TapToPay.route) {
                            inclusive = true
                        }
                    }
                },
                innerPadding = innerPadding,
                viewModel = sharedViewModel
            )
        }

        composable(route = NavigationRoutes.History.route) {
            HistoryScreen(innerPadding = innerPadding)
        }

        composable(route = NavigationRoutes.Settings.route) {
            SettingsScreen(
                innerPadding = innerPadding,
                onThemeChanged = onThemeChanged
            )
        }

        composable(
            route = NavigationRoutes.Success.route,
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType },
                navArgument("cardNumber") { type = NavType.StringType },
                navArgument("cardType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: "$0.00"
            val emvCardData by sharedViewModel.lastEmvCardData.collectAsState()

            emvCardData?.let { data ->
                SuccessScreen(
                    amount = amount,
                    emvCardData = data,
                    innerPadding = innerPadding,
                    onNavigateToDetails = {
                        navController.navigate(NavigationRoutes.CardDetail.route) {
                            popUpTo(NavigationRoutes.Home.route) {
                                inclusive = false
                            }
                        }
                    },
                    onBack = {
                        navController.popBackStack(NavigationRoutes.Home.route, inclusive = false)
                    }
                )
            }
        }

        composable(
            route = NavigationRoutes.Error.route,
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType },
                navArgument("errorMessage") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: "$0.00"
            val errorMessage = backStackEntry.arguments?.getString("errorMessage") ?: "Unknown error"

            ErrorScreen(
                amount = amount,
                errorMessage = errorMessage,
                innerPadding = innerPadding,
                onNavigateToHome = {
                    // Navigate back to home
                    navController.popBackStack(NavigationRoutes.Home.route, inclusive = false)
                }
            )
        }

        composable(route = NavigationRoutes.CardDetail.route) {
            val emvCardData by sharedViewModel.lastEmvCardData.collectAsState()

            emvCardData?.let { data ->
                CardDetailScreen(
                    emvCardData = data,
                    onBack = {
                        navController.popBackStack(NavigationRoutes.Home.route, inclusive = false)
                    }
                )
            } ?: run {
                // Fallback if no data available
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_card_data_available))
                }
            }
        }
    }
}
