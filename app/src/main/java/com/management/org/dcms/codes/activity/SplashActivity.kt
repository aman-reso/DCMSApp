package com.management.org.dcms.codes.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.management.org.dcms.R
import com.management.org.dcms.codes.HomeLandingMainActivity
import com.management.org.dcms.codes.LoginActivity
import com.management.org.dcms.codes.utility.Utility

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(Runnable {
            if (Utility.isUserLoggedIn()) {
                startHomeLandingActivity()
            }else{
                startLogintActivity()
            }
        },3000)
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
}