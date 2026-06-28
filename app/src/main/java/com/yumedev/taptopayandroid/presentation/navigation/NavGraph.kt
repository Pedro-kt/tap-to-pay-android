package com.yumedev.taptopayandroid.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yumedev.taptopayandroid.domain.model.CardInfo
import com.yumedev.taptopayandroid.domain.model.CardType
import com.yumedev.taptopayandroid.presentation.ui.screens.HistoryScreen
import com.yumedev.taptopayandroid.presentation.ui.screens.HomeScreen
import com.yumedev.taptopayandroid.presentation.ui.screens.SettingsScreen
import com.yumedev.taptopayandroid.presentation.ui.screens.SuccessScreen
import com.yumedev.taptopayandroid.presentation.ui.screens.TapToPayScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Home.route
    ) {
        composable(route = NavigationRoutes.Home.route) {
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
                onSuccess = { cardInfo ->
                    navController.navigate(
                        NavigationRoutes.Success.createRoute(
                            amount = amount,
                            cardNumber = cardInfo.cardNumber,
                            cardType = cardInfo.cardType.name
                        )
                    ) {
                        // Remove TapToPay from back stack
                        popUpTo(NavigationRoutes.TapToPay.route) {
                            inclusive = true
                        }
                    }
                },
                innerPadding = innerPadding
            )
        }

        composable(route = NavigationRoutes.History.route) {
            HistoryScreen(innerPadding = innerPadding)
        }

        composable(route = NavigationRoutes.Settings.route) {
            SettingsScreen(innerPadding = innerPadding)
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
            val cardNumber = backStackEntry.arguments?.getString("cardNumber") ?: ""
            val cardTypeString = backStackEntry.arguments?.getString("cardType") ?: "UNKNOWN"
            val cardType = try {
                CardType.valueOf(cardTypeString)
            } catch (e: IllegalArgumentException) {
                CardType.UNKNOWN
            }

            // Create a CardInfo object with the received data
            val cardInfo = CardInfo(
                cardNumber = cardNumber,
                expirationDate = "", // Not needed for success screen
                cardType = cardType
            )

            SuccessScreen(
                amount = amount,
                cardInfo = cardInfo,
                innerPadding = innerPadding,
                onNavigateToDetails = {
                    // Navigate back to home for now
                    navController.popBackStack(NavigationRoutes.Home.route, inclusive = false)
                }
            )
        }
    }
}
