package com.yumedev.taptopayandroid.ui.components

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

@Composable
fun MainBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            val itemColors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            NavigationBarItem(
                selected = currentRoute == "home",
                onClick = { onNavigate("home") },
                colors = itemColors,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Nfc,
                        contentDescription = "Cobrar",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text("Cobrar") }
            )
            // Item for future feature: History of the Data NFC
            /*
            NavigationBarItem(
                selected = currentRoute == "history",
                onClick = { onNavigate("history") },
                colors = itemColors,
                icon = {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Historial",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text("Historial") }
            )
             */

            NavigationBarItem(
                selected = currentRoute.startsWith("settings"),
                onClick = { onNavigate("settings") },
                colors = itemColors,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text("Configuración") }
            )
        }
    }
}
