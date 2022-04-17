package com.management.org.dcms.codes.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.lifecycleScope
import com.example.easywaylocation.EasyWayLocation
import com.management.org.dcms.LocationBuilder
import com.management.org.dcms.R
import com.management.org.dcms.codes.DcmsApplication
import com.management.org.dcms.codes.HomeLandingMainActivity
import com.management.org.dcms.codes.LoginActivity
import com.management.org.dcms.codes.ONE_MINUTE
import com.management.org.dcms.codes.utility.Utility
import kotlinx.coroutines.*
import java.lang.Exception

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val locationBuilder: LocationBuilder by lazy { LocationBuilder(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setUpLocationUpdatesManager()
        Handler().postDelayed(fun() {
            if (Utility.isUserLoggedIn()) {
                startHomeLandingActivity()
            } else {
                startLogintActivity()
            }
        },3000)
    }

    override fun onResume() {
        super.onResume()
        locationBuilder.startTracingLocation()
    }
    private fun startHomeLandingActivity() {
        val homeLandingIntent = Intent(this, HomeLandingMainActivity::class.java);
        startActivity(homeLandingIntent)
        finish()
    }
    private fun startLogintActivity() {
        val homeLandingIntent = Intent(this, LoginActivity::class.java);
        startActivity(homeLandingIntent)
        finish()
    }
    private  fun setUpLocationUpdatesManager() {
//        GlobalScope.launch(Dispatchers.IO) {
//            while (isActive) {
//                delay(100)
//                getLocation()
//                System.out.println("DecmsLocation-->"+ DcmsApplication.latitude)
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