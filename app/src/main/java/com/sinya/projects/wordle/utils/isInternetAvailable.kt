package com.sinya.projects.wordle.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun Context.isInternetAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnected == true
}