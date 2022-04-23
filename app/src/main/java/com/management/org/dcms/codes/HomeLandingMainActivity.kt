package com.management.org.dcms.codes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.management.org.dcms.R
import com.management.org.dcms.codes.activity.*
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.dcmsclient.additem.AddItemActivity
import com.management.org.dcms.codes.dcmsclient.viewitem.ViewItemActivity
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.TaskDetailsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.HomeViewModel
import com.management.org.dcms.databinding.ActivityMainBinding
import com.management.org.dcms.databinding.LayputHomeDashboardBinding
import com.management.org.dcms.databinding.ProfileSectionBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeLandingMainActivity : BaseActivity() {
    private val homeViewModel: HomeViewModel? by viewModels()

    private var mainActivityBinding: ActivityMainBinding? = null
    private var dashboardBinding: LayputHomeDashboardBinding? = null
    private var profileSectionBinding: ProfileSectionBinding? = null

    private var messageActionView: View? = null
    private var dataCollectionActionView: View? = null
    private var questionActionView: View? = null
    private var progressBar: ProgressBar? = null
    private var taskDetailsModel: TaskDetailsModel? = null
    private var instructionTV: TextView? = null
    private var instructionDetailTextView: TextView? = null
    private var nameTextView: TextView? = null
    private var mobNumTextView: TextView? = null
    private var logoutTextView: TextView? = null
    private var seeAddedHouseHoldTv: TextView? = null
    private var textMessageActionBtn: View? = null
    private var registrationImageURL:String="http://dcms.dmi.ac.in/content/dist/img/Registration.png"
    private var surveyImageURL:String="http://dcms.dmi.ac.in/content/dist/img/questionairs.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpViews()
        setUpObserver()
        setUpClickListener()
    }

    private fun setUpViews() {
        mainActivityBinding?.let {
            dashboardBinding = it.mainDashboardContainer
            messageActionView = dashboardBinding?.messageMainAction
            dataCollectionActionView = dashboardBinding?.dataCollectionAction
            questionActionView = dashboardBinding?.questionMainAction
            progressBar = it.progressBar
            instructionTV = it.instructionTitleTextView
            instructionDetailTextView = it.instructionDetailTextView
            logoutTextView = profileSectionBinding?.logoutSection
            mobNumTextView = profileSectionBinding?.profileContactNumTV
            nameTextView = profileSectionBinding?.profileNameTV
            seeAddedHouseHoldTv = it.seeAddedHouseHoldTv
            textMessageActionBtn = dashboardBinding?.textMessageAction
        }
        mainActivityBinding?.containerAppBar?.appBarTitleTV?.showHideView(false)
        mainActivityBinding?.containerAppBar?.toolbar?.showHideView(true)
        mainActivityBinding?.containerAppBar?.toolbar?.setTitle(R.string.app_name)
        mainActivityBinding?.containerAppBar?.toolbar?.setOnMenuItemClickListener {
            if (it.itemId == R.id.profile) {
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                performLogoutOperation()
            }
            true
        }
        if (dashboardBinding?.imageViewForRegistration!=null) {
            Picasso.get().load(registrationImageURL).into(dashboardBinding?.imageViewForRegistration)
        }
        if (dashboardBinding?.imageViewForSurvey!=null){
            Picasso.get().load(surveyImageURL).into(dashboardBinding?.imageViewForSurvey)
        }
       // dashboardBinding?.imageViewForRegistration?.
    }


    private fun setUpObserver() {
        homeViewModel?.getTaskDetails()
        progressBar?.showHideView(true)
        homeViewModel?.apply {
            taskDetailLiveData.observe(this@HomeLandingMainActivity) {
                progressBar?.showHideView(false)
                parseNetworkResponse(it)
            }
        }
    }

    private fun parseNetworkResponse(response: GlobalNetResponse<TaskDetailsModel>?) {
        when (response) {
            is GlobalNetResponse.NetworkFailure -> {
                val message = response.error
                Utility.showToastMessage(message = message)
            }
            is GlobalNetResponse.Success -> {
                val successResponse: TaskDetailsModel = response.value
                this.taskDetailsModel = successResponse
                setUpContentForViews()
            }
            else -> {

            }
        }
    }

    private fun setUpContentForViews() {
        taskDetailsModel?.let { taskDetailsModel: TaskDetailsModel ->
            val isQuestionEnabled = taskDetailsModel.Task?.Questionaires
            val isMessageEnabled = taskDetailsModel.Task?.WAMessages
            val instruction = taskDetailsModel.Task?.Instructions
            val isDataCollectionEnabled = taskDetailsModel.Task?.DataCollection
            val isTextMessageEnabled = taskDetailsModel.Task?.TextMessage
        }
    }

    private fun setUpClickListener() {
        questionActionView?.setOnClickListener {
            if (taskDetailsModel != null) {
                val isQuestionEnabled = taskDetailsModel?.Task?.Questionaires
                if (isQuestionEnabled == true) {
                    startQuestionActivity()
                } else {
                    //startQuestionActivity()
                    Utility.showToastMessage(getString(R.string.not_enabled_right_now))
                }
            } else {
                Utility.showToastMessage(getString(R.string.please_wait))
            }
        }
        messageActionView?.setOnClickListener {
            if (taskDetailsModel != null) {
                System.out.println(taskDetailsModel?.Task)
                val isMessageEnabled = taskDetailsModel?.Task?.WAMessages
                if (isMessageEnabled == true) {
                    startMessageTemplateActivity()
                } else {
                    // startMessageTemplateActivity()
                    Utility.showToastMessage(getString(R.string.not_enabled_right_now))
                }
            } else {
                Utility.showToastMessage(getString(R.string.please_wait))
            }
        }
        dataCollectionActionView?.setOnClickListener {
            if (taskDetailsModel != null) {
                val isMessageEnabled = taskDetailsModel?.Task?.DataCollection
                if (isMessageEnabled == true) {
                    startDataCollectionActivity()
                } else {
                    //startDataCollectionActivity()
                    Utility.showToastMessage(getString(R.string.not_enabled_right_now))
                }
            } else {
                Utility.showToastMessage(getString(R.string.please_wait))
            }
        }
        textMessageActionBtn?.setOnClickListener {
            if (taskDetailsModel != null) {
                val isMessageEnabled = taskDetailsModel?.Task?.TextMessage
                if (isMessageEnabled == true) {
                    startTextSmsActivity()
                } else {
                    // startTextSmsActivity()
                     Utility.showToastMessage(getString(R.string.not_enabled_right_now))
                }
            } else {
                Utility.showToastMessage(getString(R.string.please_wait))
            }
        }
        seeAddedHouseHoldTv?.setOnClickListener {
            startSeeAddedHouseHoldIntent()
        }
    }

    private fun startQuestionActivity() {
        if (taskDetailsModel?.Task != null) {
            val intent = Intent(this, ContactsListingForQuestionActivity::class.java)
            // intent.putExtra(URL_TO_BE_LOAD, response)
            startActivity(intent)
        }
    }


    private fun startMessageTemplateActivity() {
        val intent = Intent(this, MessageTemplateActivity::class.java)
        startActivity(intent)
    }

    private fun startDataCollectionActivity() {
        val intent = Intent(this, AddItemActivity::class.java)
        startActivity(intent)
    }

    private fun redirectedToSignInPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startSeeAddedHouseHoldIntent() {
        val intent = Intent(this, ViewItemActivity::class.java)
        startActivity(intent)
    }

    private fun startTextSmsActivity() {
        val intent = Intent(this, SendTextSMSActivity::class.java)
        startActivity(intent)
    }

    private fun performLogoutOperation() {
        if (Utility.isUserLoggedIn()) {
            progressBar?.showHideView(true)
            homeViewModel?.performLogoutOperationForServer(callbackForLogoutResponse)
        } else {
            progressBar?.showHideView(false)
            Utility.showToastMessage("Already logout")
        }
    }

    private var callbackForLogoutResponse = fun(isSuccessfullyLogout: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (isSuccessfullyLogout) {
                AuthConfigManager.logoutUser();
                Utility.showToastMessage(getString(R.string.log_out_message))
                redirectedToSignInPage()
            } else {
                Utility.showToastMessage(getString(R.string.something_went_wrong))
            }
            progressBar?.showHideView(false)
        }
    }
}