package com.management.org.dcms.codes.dcmsclient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.R
import com.management.org.dcms.codes.dcmsclient.login.DcmsClientLoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class DcmsSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dcms_client_splash_activity)
        lifecycleScope.launch {
            delay(1000)
            Timber.e("${Manager(application).isLoggedIn}")
            Timber.e("${Manager(application).token}")
            if(!Manager(application).isLoggedIn) {
                startActivity(
                    Intent(
                        this@DcmsSplashActivity,
                        DcmsClientLoginActivity::class.java
                    )
                )
            }else{
                startActivity(
                    Intent(
                        this@DcmsSplashActivity,
                        DcmClientMainActivity::class.java
                    )
                )
            }
            finish()
        }
    }
}