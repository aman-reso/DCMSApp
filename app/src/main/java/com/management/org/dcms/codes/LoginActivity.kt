package com.management.org.dcms.codes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.MemoryFile
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.management.org.dcms.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setUpViews()
        setUpClickListener()
    }

    private fun setUpViews() {
        loginActivityBinding?.let { activityLoginBinding ->
            phoneNumberET = activityLoginBinding.editTextPhoneNumber
            passwordET = activityLoginBinding.editTextPassword
            loginBtn = activityLoginBinding.loginButton
        }
    }

    private fun setUpClickListener() {
        loginBtn?.setOnClickListener {
            val inputMobileNumber: String = phoneNumberET?.text.toString()
            val inputPassword: String = passwordET?.text.toString()
            if (inputMobileNumber.isNotEmpty()) {
                if (inputPassword.isNotEmpty()) {
                    //proceed networkCall
                    loginViewModel?.submitLoginData(inputPhone = inputMobileNumber, inputPassword = inputPassword)
                }
            }
        }
    }
}