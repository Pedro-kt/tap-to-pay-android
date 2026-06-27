package com.yumedev.taptopayandroid

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yumedev.taptopayandroid.ui.components.MainBottomBar
import com.yumedev.taptopayandroid.ui.screens.HomeScreen
import com.yumedev.taptopayandroid.ui.theme.TapToPayAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TapToPayAndroidTheme {
                var currentRoute by remember { mutableStateOf("home") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MainBottomBar(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                currentRoute = route
                                // TODO: Implement actual navigation when other screens are ready
                                if (route != "home") {
                                    Toast.makeText(
                                        this,
                                        "Navegando a: $route",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    when (currentRoute) {
                        "home" -> {
                            HomeScreen(
                                onGenerateQr = { amount ->
                                    // TODO: Implement QR generation
                                    Toast.makeText(
                                        this,
                                        "Generando QR para: $$amount",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                innerPadding = innerPadding
                            )
                        }
                        "history" -> {
                            // TODO: Implement History Screen
                        }
                        "settings" -> {
                            // TODO: Implement Settings Screen
                        }
                    }
                }
            }
        }
    }
}