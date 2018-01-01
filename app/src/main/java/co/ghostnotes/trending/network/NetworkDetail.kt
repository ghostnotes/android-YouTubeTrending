package co.ghostnotes.trending.network

import android.content.Context
import android.net.ConnectivityManager

class NetworkDetail(private val context: Context) {

    fun isDeviceOnline(): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

}