package com.management.org.dcms.codes.utility

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
}