package com.management.org.dcms.codes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.R
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.utility.NetworkImpl
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.ProfileViewModel
import com.management.org.dcms.databinding.ActivityChangePasswordBinding
import com.management.org.dcms.databinding.ActivityForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity() {
    private val profileViewModel: ProfileViewModel? by viewModels()
    private var parentBinding: ActivityForgotPasswordBinding? = null
    private var mobileNumEt: AppCompatEditText? = null
    private var otpEditText: AppCompatEditText? = null
    private var pgBar: ProgressBar? = null
    private var forgotPassActionBtn: AppCompatButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        setUpHeaders()
        setUpViews()
        setUpClickListener()
    }

    private fun setUpHeaders() {
        parentBinding?.containerAppBar?.icNavBackIcon?.showHideView(true)
        parentBinding?.containerAppBar?.icNavBackIcon?.setOnClickListener {
            onBackPressed()
        }
        parentBinding?.containerAppBar?.appBarTitleTV?.text = getString(R.string.forgot_pass)
    }

    private fun setUpViews() {
        pgBar = parentBinding?.progressBar
        forgotPassActionBtn = parentBinding?.forgotPassActionBtn
        otpEditText = parentBinding?.forgotEnterOTPEt
        mobileNumEt = parentBinding?.forgotPasswordMobNumET
    }

    private fun setUpClickListener() {
        forgotPassActionBtn?.setOnClickListener {
            if (NetworkImpl.checkNetworkStatus()) {
                if (validateInput()) {
                    pgBar?.showHideView(true)
                    val mobileNum = mobileNumEt?.text?.toString()
                    if (mobileNum != null) {
                        profileViewModel?.forgotUserPassword(mobileNum) { message, status ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                pgBar?.showHideView(false)
                                if (message != null) {
                                    Utility.showToastMessage(message)
                                }
                                if (status == 1) {
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        if (mobileNumEt?.text?.isNotEmpty() == true) {
            if (mobileNumEt?.text.toString().length == 10) {
                return true
            } else {
                Utility.showToastMessage(getString(R.string.enter_correct_mobile_number))
            }
        } else {
            Utility.showToastMessage(getString(R.string.mobile_num_empty))
        }
        return false
    }
}