package com.yumedev.taptopayandroid.presentation.navigation

sealed class NavigationRoutes(val route: String) {
    data object Home : NavigationRoutes("home")
    data object History : NavigationRoutes("history")
    data object Settings : NavigationRoutes("settings")
}
