package com.management.org.dcms.codes.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.easywaylocation.EasyWayLocation
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.management.org.dcms.LocationBuilder
import com.management.org.dcms.R
import com.management.org.dcms.codes.DcmsApplication
import com.management.org.dcms.codes.HomeLandingMainActivity
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.utility.Utility.checkIsOnline
import kotlinx.coroutines.*
import java.util.*


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    var pgBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        pgBar = findViewById(R.id.pgBar)
        pgBar?.showHideView(true)
        if (!checkIsOnline()) {
            Utility.showToastMessage(LanguageManager.getStringInfo(R.string.no_internet_connection))
        }
        setUpLanguage(LanguageManager.getLanguageCode())
    }

    private fun setUpLanguage(languageCode: String) {
        val config = resources.configuration
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun navigateForward() {
        lifecycleScope.launch {
            if (LanguageManager.getShownLangSettingFirstTime()) {
                if (Utility.isUserLoggedIn()) {
                    startHomeLandingActivity()
                } else {
                    startLoginActivity()
                }
            } else {
                startLanguageIntroActivity()
            }
        }
    }

    override fun currentLocation(location: Location?) {
        super.currentLocation(location)
        navigateForward()
    }

    override fun locationCancelled() {
        super.locationCancelled()
        Utility.showToastMessage("Please allow permission")
        finish()
    }

    private fun startLoginActivity() {
        val homeLandingIntent = Intent(this, LoginActivity::class.java)
        startActivity(homeLandingIntent)
        finish()
    }

    private fun startLanguageIntroActivity() {
        val homeLandingIntent = Intent(this, ChangeLanguageActivity::class.java)
        startActivity(homeLandingIntent)
        finish()
    }

    private fun startHomeLandingActivity() {
        val homeLandingIntent = Intent(this@SplashActivity, HomeLandingMainActivity::class.java)
        startActivity(homeLandingIntent)
        finish()
    }


}