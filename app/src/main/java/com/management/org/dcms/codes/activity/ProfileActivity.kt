package com.management.org.dcms.codes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
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
class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null

    private val viewModel: ProfileViewModel? by viewModels()
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        if (!Utility.isUserLoggedIn()) {
            finish()
            Utility.showToastMessage("Please Login")
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
        binding?.containerAppBar?.icNavBackIcon?.setOnClickListener { onBackPressed() }
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
                val successResponse: ProfileResponseModel? = response.value as ProfileResponseModel?
                if (successResponse != null) {
                    try {
                        bindResponseWithViews(successResponse.profile)

                    } catch (e: Exception) {

                    }
                }
            }
            is GlobalNetResponse.NetworkFailure -> {

            }
        }
    }

    @Throws(Exception::class)
    private fun bindResponseWithViews(profile: Profile?) {
        //need to wrap all them into html tag
        binding?.profileContactNumTV?.text = profile?.MobileNo
        binding?.profileNameTV?.text = profile?.Name
        binding?.profileBlockNameTV?.text = profile?.BlockName
        binding?.profileEmailIdTV?.text = profile?.EmailId
        binding?.profileStateNameTV?.text = profile?.StateName
        binding?.profileVillageNameTV?.text = profile?.VillageName
        binding?.profileDistrictNameTV?.text = profile?.DistrictName
        binding?.profilePanchayatNameTV?.text = profile?.GramPanchayatName
    }
}