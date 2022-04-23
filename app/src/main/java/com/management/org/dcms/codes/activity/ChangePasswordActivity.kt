package com.management.org.dcms.codes.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.R
import com.management.org.dcms.codes.HomeLandingMainActivity
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.utility.NetworkImpl
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.ProfileViewModel
import com.management.org.dcms.databinding.ActivityChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity() {
    private val profileViewModel: ProfileViewModel? by viewModels()
    private var parentBinding: ActivityChangePasswordBinding? = null
    private var oldPassEditText: AppCompatEditText? = null
    private var newPassEditText: AppCompatEditText? = null
    private var confirmPassEditText: AppCompatEditText? = null
    private var pgBar: ProgressBar? = null
    private var changePassActionBtn: AppCompatButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        setUpHeaders()
        setUpViews()
        setUpClickListener()
    }

    private fun setUpHeaders() {
        parentBinding?.containerAppBar?.icNavBackIcon?.showHideView(true)
        parentBinding?.containerAppBar?.icNavBackIcon?.setOnClickListener {
            onBackPressed()
        }
        parentBinding?.containerAppBar?.appBarTitleTV?.text = getString(R.string.change_password)
    }

    private fun setUpViews() {
        oldPassEditText = parentBinding?.changePasswordOldPass
        newPassEditText = parentBinding?.changePasswordNewPass
        pgBar = parentBinding?.progressBar
        confirmPassEditText = parentBinding?.changePasswordConfirmPass
        changePassActionBtn = parentBinding?.changePasswordActionBtn
    }

    private fun setUpClickListener() {
        changePassActionBtn?.setOnClickListener {
            if (NetworkImpl.checkNetworkStatus()) {
                if (validateInput()) {
                    val oldPassword: String? = oldPassEditText?.text?.toString()
                    val newPassword: String? = newPassEditText?.text?.toString()
                    val confirmPassword: String? = confirmPassEditText?.text?.toString()
                    if (oldPassword != null && newPassword != null && confirmPassword != null) {
                        pgBar?.showHideView(true)
                        profileViewModel?.changeProfilePassword(oldPassword, newPassword, confirmPassword) { message, status ->
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
        if (oldPassEditText?.text?.isNotEmpty() == true) {
            if (newPassEditText?.text?.isNotEmpty() == true) {
                if (confirmPassEditText?.text?.isNotEmpty() == true) {
                    if (newPassEditText?.text.toString() == confirmPassEditText?.text.toString()) {
                        return true
                    } else {
                        Utility.showToastMessage(getString(R.string.new_password_mismatch))
                    }
                } else {
                    Utility.showToastMessage(getString(R.string.confirm_pass_is_empty))
                }
            } else {
                Utility.showToastMessage(getString(R.string.new_pass_is_empty))
            }
        } else {
            Utility.showToastMessage(getString(R.string.old_pass_is_empty))
        }
        return false
    }


}