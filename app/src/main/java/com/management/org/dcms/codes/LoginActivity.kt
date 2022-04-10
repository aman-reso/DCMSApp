package com.management.org.dcms.codes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.management.org.dcms.R
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.LoginResponseData
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.utility.Utility.showToastMessage
import com.management.org.dcms.codes.viewmodel.logintvm.LoginViewModel
import com.management.org.dcms.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel? by viewModels()
    private var loginActivityBinding: ActivityLoginBinding? = null
    private var phoneNumberET: EditText? = null
    private var passwordET: EditText? = null
    private var loginBtn: Button? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setUpViews()
        setUpObserves()
        setUpClickListener()
    }

    private fun setUpViews() {
        loginActivityBinding?.let { activityLoginBinding ->
            phoneNumberET = activityLoginBinding.editTextPhoneNumber
            passwordET = activityLoginBinding.editTextPassword
            loginBtn = activityLoginBinding.loginButton
            progressBar = activityLoginBinding.loginPgBar
        }
    }

    private fun setUpObserves() {
        loginViewModel?.apply {
            this.authLiveData.observe(this@LoginActivity) { globalNetworkResponse ->
                parseLoginDataResponse(globalNetworkResponse)
            }
        }
    }

    private fun parseLoginDataResponse(response: GlobalNetResponse<LoginResponseData>) {
        progressBar?.showHideView(false)
        when (response) {
            is GlobalNetResponse.NetworkFailure -> {
                val errorMsg = response.error
                showToastMessage(errorMsg)
            }
            is GlobalNetResponse.Success -> {
                val loginResponseData: LoginResponseData? = response.value
                if (loginResponseData?.authToken != null) {
                    AuthConfigManager.saveAuthToken(loginResponseData.authToken)
                    startHomeLandingActivity()
                }
            }
        }
    }

    private fun setUpClickListener() {
        loginBtn?.setOnClickListener {
            val inputMobileNumber: String = phoneNumberET?.text.toString()
            val inputPassword: String = passwordET?.text.toString()
            if (inputMobileNumber.isNotEmpty()) {
                if (inputPassword.isNotEmpty()) {
                    progressBar?.showHideView(true)
                    loginViewModel?.submitLoginData(inputPhone = inputMobileNumber, inputPassword = inputPassword)
                }
            }
        }
    }

    private fun startHomeLandingActivity() {
        val homeLandingIntent = Intent(this, HomeLandingMainActivity::class.java);
        startActivity(homeLandingIntent)
        finish()
    }
}