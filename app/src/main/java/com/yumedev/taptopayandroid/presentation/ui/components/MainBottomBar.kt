package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yumedev.taptopayandroid.presentation.navigation.NavigationRoutes

@Composable
fun MainBottomBar(
    currentRoute: String,
    navController: NavHostController
) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            val itemColors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary,
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            )

            NavigationBarItem(
                selected = currentRoute == NavigationRoutes.Home.route,
                onClick = {
                    navController.navigate(NavigationRoutes.Home.route) {
                        popUpTo(NavigationRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = itemColors,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Nfc,
                        contentDescription = "Payment",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text("Payment") }
            )
            // Item for future feature: History of the Data NFC
            /*
            NavigationBarItem(
                selected = currentRoute == NavigationRoutes.History.route,
                onClick = {
                    navController.navigate(NavigationRoutes.History.route) {
                        popUpTo(NavigationRoutes.Home.route)
                        launchSingleTop = true
                    }
                },
                colors = itemColors,
                icon = {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text("History") }
            )
             */

            NavigationBarItem(
                selected = currentRoute.startsWith(NavigationRoutes.Settings.route),
                onClick = {
                    navController.navigate(NavigationRoutes.Settings.route) {
                        popUpTo(NavigationRoutes.Home.route)
                        launchSingleTop = true
                    }
                },
                colors = itemColors,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text("Settings") }
            )
        }
    }
}
