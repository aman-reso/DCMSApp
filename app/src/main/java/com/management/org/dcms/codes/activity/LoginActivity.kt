package com.management.org.dcms.codes.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import com.management.org.dcms.R
import com.management.org.dcms.codes.HomeLandingMainActivity
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.dcmsclient.signup.SignupActivity
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.LoginResponseData
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.utility.Utility.showToastMessage
import com.management.org.dcms.codes.viewmodel.LoginViewModel
import com.management.org.dcms.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint


const val ONE_MINUTE: Long = 20 * 1000

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
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
                } else {
                    if (loginResponseData?.message != null) {
                        loginActivityBinding?.root?.let { showToastMessage(loginResponseData.message!!) }
                    }
                }
            }
        }
    }

    private fun setUpClickListener() {
        loginBtn?.setOnClickListener {
            val inputMobileNumber: String = phoneNumberET?.text.toString()
            val inputPassword: String = passwordET?.text.toString()
            if (inputMobileNumber.isNotEmpty()) {
                if (inputMobileNumber.length != 10) {
                    showToastMessage(getString(R.string.enter_correct_mobile_number))
                    return@setOnClickListener
                }
                if (inputPassword.isNotEmpty()) {
                    progressBar?.showHideView(true)
                    loginViewModel?.submitLoginData(inputPhone = inputMobileNumber, inputPassword = inputPassword)
                } else {
                    showToastMessage(getString(R.string.password_is_empty))
                }
            } else {
                showToastMessage(getString(R.string.mobile_num_empty))
            }
        }
        if (Utility.isUserLoggedIn()) {
            loginActivityBinding?.registerBtn?.showHideView(false)
        } else {
            loginActivityBinding?.registerBtn?.showHideView(true)
        }
        loginActivityBinding?.registerBtn?.setOnClickListener {
            startRegistrationActivity()
        }
        loginActivityBinding?.forgotPasswordTv?.setOnClickListener {
            startForgotPasswordActivity()
        }
    }

    private fun startForgotPasswordActivity() {
        val forgotPasswordIntent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(forgotPasswordIntent)
    }

    private fun startHomeLandingActivity() {
        val homeLandingIntent = Intent(this, HomeLandingMainActivity::class.java)
        startActivity(homeLandingIntent)
        finish()
    }

    private fun startRegistrationActivity() {
        val signupActivityIntent = Intent(this, SignupActivity::class.java)
        startActivity(signupActivityIntent)
    }

}
