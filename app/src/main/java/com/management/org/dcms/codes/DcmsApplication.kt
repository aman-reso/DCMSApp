package com.management.org.dcms.codes

import android.app.Application
import android.content.Context
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DcmsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        applicationInstance = applicationContext

    }

    companion object {
        private var applicationInstance: Context? = null
        fun getDcmsAppContext(): Context? {
            return applicationInstance
        }
    }

}