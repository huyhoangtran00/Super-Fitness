package com.example.superfitness.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.example.superfitness.domain.connectivity.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class ConnectivityObserverImpl(
    private val context: Context
): ConnectivityObserver {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!
    override val isConnected: Flow<Boolean>
        get() = callbackFlow {
            val currentNetwork = connectivityManager.activeNetwork
            val currentNetworkCapabilities = connectivityManager
                .getNetworkCapabilities(currentNetwork)
            val isCurrentlyConnected = currentNetworkCapabilities
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
            trySend(isCurrentlyConnected)

            val callback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(false)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(false)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                    trySend(connected)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose{
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged() // Avoid duplicated emit
}