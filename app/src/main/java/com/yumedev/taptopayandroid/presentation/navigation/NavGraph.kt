package com.yumedev.taptopayandroid.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yumedev.taptopayandroid.presentation.ui.screens.HistoryScreen
import com.yumedev.taptopayandroid.presentation.ui.screens.HomeScreen
import com.yumedev.taptopayandroid.presentation.ui.screens.SettingsScreen
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
                innerPadding = innerPadding
            )
        }

        composable(route = NavigationRoutes.History.route) {
            HistoryScreen(innerPadding = innerPadding)
        }

        composable(route = NavigationRoutes.Settings.route) {
            SettingsScreen(innerPadding = innerPadding)
        }
    }
}
