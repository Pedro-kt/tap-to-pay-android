package com.yumedev.taptopayandroid.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yumedev.taptopayandroid.ui.screens.HistoryScreen
import com.yumedev.taptopayandroid.ui.screens.HomeScreen
import com.yumedev.taptopayandroid.ui.screens.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Home.route
    ) {
        composable(route = NavigationRoutes.Home.route) {
            HomeScreen(
                onGeneratePayment = { amount ->
                    // TODO: Implement NFC payment processing
                    Toast.makeText(
                        context,
                        "Processing payment: $$amount",
                        Toast.LENGTH_SHORT
                    ).show()
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
