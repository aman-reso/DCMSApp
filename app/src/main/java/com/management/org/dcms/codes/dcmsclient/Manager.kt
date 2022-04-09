package com.management.org.dcms.codes.dcmsclient

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class Manager(application: Application) {

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val AUTH_TOKEN = "auth_token"
    }


    var pref: SharedPreferences = application.getSharedPreferences("FirstApp", Context.MODE_PRIVATE)
    private val editor = pref.edit()


    var isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGGED_IN, false)
        set(value) {
            editor.putBoolean(IS_LOGGED_IN, value).apply()
        }
    var token: String?
        get() = pref.getString(AUTH_TOKEN, "")
        set(value) {
            editor.putString(AUTH_TOKEN, value).apply()
        }
}