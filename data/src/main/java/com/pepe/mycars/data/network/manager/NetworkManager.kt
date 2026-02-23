package com.pepe.mycars.data.network.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.pepe.mycars.domain.manager.INetworkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : INetworkManager {
        private val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        override val isConnected: Flow<Boolean> =
            callbackFlow {
                val networkCallback =
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            trySend(true)
                        }

                        override fun onLost(network: Network) {
                            trySend(false)
                        }

                        override fun onUnavailable() {
                            trySend(false)
                        }
                    }

                val request =
                    NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()

                connectivityManager.registerNetworkCallback(request, networkCallback)

                val activeNetwork = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                trySend(capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)

                awaitClose {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
            }.distinctUntilChanged()
    }
