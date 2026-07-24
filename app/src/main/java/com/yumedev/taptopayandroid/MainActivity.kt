package com.yumedev.taptopayandroid

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import com.yumedev.taptopayandroid.domain.usecase.HandleNfcTagUseCase
import com.yumedev.taptopayandroid.presentation.navigation.NavGraph
import com.yumedev.taptopayandroid.presentation.ui.components.MainBottomBar
import com.yumedev.taptopayandroid.presentation.ui.theme.TapToPayAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var handleNfcTagUseCase: HandleNfcTagUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var themeMode by remember { mutableStateOf(preferencesRepository.getThemeMode()) }

            val darkTheme = when (themeMode) {
                PreferencesRepository.THEME_LIGHT -> false
                PreferencesRepository.THEME_DARK -> true
                PreferencesRepository.THEME_SYSTEM -> systemInDarkTheme
                else -> systemInDarkTheme
            }

            val onThemeChanged: (String) -> Unit = { newTheme ->
                themeMode = newTheme
                preferencesRepository.setThemeMode(newTheme)
            }

            TapToPayAndroidTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"

                // Hide bottom bar on these screens
                val shouldShowBottomBar = when {
                    currentRoute.startsWith("tap_to_pay") -> false
                    currentRoute.startsWith("success") -> false
                    currentRoute.startsWith("error") -> false
                    currentRoute.startsWith("card_detail") -> false
                    else -> true
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (shouldShowBottomBar) {
                            MainBottomBar(
                                currentRoute = currentRoute,
                                navController = navController
                            )
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        innerPadding = innerPadding,
                        onThemeChanged = onThemeChanged
                    )
                }
            }
        }

        // Handle NFC intent if activity was launched with one
        handleNfcIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent == null) return

        val action = intent.action
        Log.d(TAG, "NFC Intent received: $action")

        if (action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            action == NfcAdapter.ACTION_TAG_DISCOVERED
        ) {

            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                Log.d(TAG, "NFC Tag detected: ${tag.id.contentToString()}")

                lifecycleScope.launch {
                    handleNfcTagUseCase(tag)
                }
            } else {
                Log.w(TAG, "NFC Tag is null")
            }
        }
    }
}