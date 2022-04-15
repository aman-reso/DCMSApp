package com.management.org.dcms.codes.authConfig

import android.content.Context
import com.management.org.dcms.codes.DcmsApplication

class AuthConfigManager {
    companion object {
        var shared_pref_location = "com.dcms.share_pref"
        var tokenKey: String = "token";

        fun saveAuthToken(token: String?) {
            if (token != null) {
                val sharedPref = DcmsApplication.getDcmsAppContext()?.getSharedPreferences(shared_pref_location, Context.MODE_PRIVATE);
                sharedPref?.edit()?.putString(tokenKey, token)?.apply()
            }
        }

        fun getAuthToken(): String {
            val sharedPref = DcmsApplication.getDcmsAppContext()?.getSharedPreferences(shared_pref_location, Context.MODE_PRIVATE);
            return sharedPref?.getString(tokenKey, "") ?: ""
        }

        fun logoutUser() {
            val sharedPref = DcmsApplication.getDcmsAppContext()?.getSharedPreferences(shared_pref_location, Context.MODE_PRIVATE);
            sharedPref?.edit()?.putString(tokenKey, "")?.apply()
        }


    }

}