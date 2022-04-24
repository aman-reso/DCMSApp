package com.management.org.dcms.codes.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.utility.Utility.checkIsOnline
import kotlinx.coroutines.*
import java.util.*


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private val locationBuilder: LocationBuilder by lazy { LocationBuilder(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!checkIsOnline()) {
            Utility.showToastMessage(LanguageManager.getStringInfo(R.string.no_internet_connection))
        }
        setUpLanguage(LanguageManager.getLanguageCode())
        setUpPermissions()
        //setUpLocationUpdatesManager()
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

    override fun onResume() {
        super.onResume()
        locationBuilder.startTracingLocation()
    }

    private fun navigateForward() {
        lifecycleScope.launch {
            delay(3000)
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

    private fun setUpPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {
                        if (it.areAllPermissionsGranted()) {
                            //move forward
                            navigateForward()
                        } else {
                            Utility.showToastMessage(LanguageManager.getStringInfo(R.string.please_allow_all_above_permission))
                            finish()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) { /* ... */

                }
            }).check()
    }

    private fun setUpLocationUpdatesManager() {
//        GlobalScope.launch(Dispatchers.IO) {
//            while (isActive) {
//                delay(100)
//                getLocation()
//                System.out.println("DcmsLocation-->"+ DcmsApplication.latitude)
//                delay(ONE_MINUTE)
//            }
//        }
    }

    private fun getLocation() {
        try {
            if (locationBuilder.getLocation() != null && locationBuilder.getLocation()?.first != null) {
                DcmsApplication.latitude = locationBuilder.getLocation()?.first!!
            }
        } catch (e: Exception) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EasyWayLocation.LOCATION_SETTING_REQUEST_CODE -> {
                locationBuilder.easyWayLocation?.onActivityResult(resultCode)
            }
        }
    }
}