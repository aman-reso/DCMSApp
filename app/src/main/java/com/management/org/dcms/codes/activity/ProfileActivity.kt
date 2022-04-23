package com.management.org.dcms.codes.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.management.org.dcms.R
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.Profile
import com.management.org.dcms.codes.models.ProfileResponseModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.ProfileViewModel
import com.management.org.dcms.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    private var binding: ActivityProfileBinding? = null

    private val viewModel: ProfileViewModel? by viewModels()
    private var progressBar: ProgressBar? = null
    private var profileWebView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        if (!Utility.isUserLoggedIn()) {
            with(Utility) { showToastMessage(getString(R.string.please_login)) }
            finish()
        }
        setUpViews()
        setUpObservers()
    }

    private fun setUpViews() {
        //use extension function for showHide views
        binding?.containerAppBar?.toolbar?.visibility = View.GONE
        binding?.containerAppBar?.icNavBackIcon?.visibility = View.VISIBLE
        binding?.containerAppBar?.appBarTitleTV?.visibility = View.VISIBLE
        progressBar = binding?.progressBar
        profileWebView = binding?.profileWebView
        binding?.containerAppBar?.icNavBackIcon?.setOnClickListener { onBackPressed() }

        binding?.changePasswordWhenOnProfile?.setOnClickListener {
            moveToChangePasswordActivity()
        }
    }

    private fun setUpObservers() {
        viewModel?.profileInfoLiveData?.observe(this) { response ->
            progressBar?.showHideView(false)
            if (response != null) {
                parseResponse(response)
            }
        }
        progressBar?.showHideView(true)
        viewModel?.getProfileInfo()
    }

    private fun parseResponse(response: GlobalNetResponse<*>) {
        when (response) {
            is GlobalNetResponse.Success -> {
                val successResponse: JsonObject? = response.value as JsonObject
                if (successResponse != null) {
                    try {
                        if (successResponse.has("Message")) {
                            val message = successResponse.get("Message").asString
                            loadWebViewWithHtml(message)
                        }
                    } catch (e: Exception) {
                        profileWebView?.showHideView(false)
                    }
                }
            }
            is GlobalNetResponse.NetworkFailure -> {
                Utility.showToastMessage(getString(R.string.something_went_wrong))
            }
        }
    }

    private fun loadWebViewWithHtml(infoString: String) {
        profileWebView?.showHideView(true)
        profileWebView?.loadDataWithBaseURL(null, infoString, "text/html", "utf-8", null)
    }

    private fun destroyWebView() {
        profileWebView?.destroy()
        profileWebView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyWebView()
    }

    private fun moveToChangePasswordActivity() {
        startActivity(Intent(this, ChangePasswordActivity::class.java))
    }
}