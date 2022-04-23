package com.management.org.dcms.codes.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.management.org.dcms.codes.DcmsApplication
import com.management.org.dcms.codes.authConfig.AuthConfigManager


object Utility {
    fun isUserLoggedIn(): Boolean {
        val userToken: String? = AuthConfigManager.getAuthToken()
        if (userToken.isNullOrEmpty()) {
            return false
        }
        return true
    }

    fun showToastMessage(message: String) {
        Toast.makeText(DcmsApplication.getDcmsAppContext(), message, Toast.LENGTH_LONG).show()
    }
    fun checkIsOnline():Boolean{
        val connMgr: ConnectivityManager = DcmsApplication.getDcmsAppContext()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                return true
            } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                return true
            }
        }
        return false
    }
}