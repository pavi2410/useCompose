package me.pavi2410.useCompose.network

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService

/**
 * A hook to monitor active network connection.
 */
@RequiresPermission(ACCESS_NETWORK_STATE)
@SuppressLint("ComposableNaming")
@Composable
fun useConnectionStatus(): Boolean {
    val context = LocalContext.current

    var isConnected by remember { mutableStateOf(false) }

    DisposableEffect(true) {
        val connectivityManager = context.getSystemService<ConnectivityManager>()

        val callback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = connectivityManager?.let { isNetworkConnected(it) } ?: true
            }

            override fun onLost(network: Network) {
                isConnected = connectivityManager?.let { isNetworkConnected(it) } ?: false
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager?.registerNetworkCallback(networkRequest, callback)

        if (connectivityManager != null) {
            isConnected = isNetworkConnected(connectivityManager)
        }

        onDispose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }

    return isConnected
}

private fun isNetworkConnected(connectivityManager: ConnectivityManager): Boolean {
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}