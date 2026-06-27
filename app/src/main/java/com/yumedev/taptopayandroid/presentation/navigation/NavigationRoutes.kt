package com.yumedev.taptopayandroid.presentation.navigation

sealed class NavigationRoutes(val route: String) {
    data object Home : NavigationRoutes("home")
    data object History : NavigationRoutes("history")
    data object Settings : NavigationRoutes("settings")
    data object TapToPay : NavigationRoutes("tap_to_pay/{amount}") {
        fun createRoute(amount: String) = "tap_to_pay/$amount"
    }
}
