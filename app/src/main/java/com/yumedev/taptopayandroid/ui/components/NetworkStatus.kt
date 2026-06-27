package com.yumedev.taptopayandroid.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect

enum class NetworkStatus {
    Available,
    Unavailable,
    Losing,
    Lost
}

@Composable
fun rememberNetworkStatus(): NetworkStatus {
    val context = LocalContext.current
    var networkStatus by remember { mutableStateOf(NetworkStatus.Available) }

    LifecycleStartEffect(Unit) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                networkStatus = NetworkStatus.Available
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                networkStatus = NetworkStatus.Losing
            }

            override fun onLost(network: Network) {
                networkStatus = NetworkStatus.Lost
            }

            override fun onUnavailable() {
                networkStatus = NetworkStatus.Unavailable
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, networkCallback)

        onStopOrDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    return networkStatus
}
