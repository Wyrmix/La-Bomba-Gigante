package com.wyrmix.giantbombvideoplayer.video.network

import android.content.Context
import android.net.ConnectivityManager



/**
 * Created by kylea
 *
 * 10/11/2018 at 5:53 PM
 */
class NetworkManager(private val context: Context) {
    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && (activeNetworkInfo.isConnected)
        }
}
