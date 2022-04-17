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
import com.management.org.dcms.codes.activity.MessageTemplateActivity
import com.management.org.dcms.codes.activity.ProfileActivity
import com.management.org.dcms.codes.activity.QuestionListingActivity
import com.management.org.dcms.codes.activity.SendTextSMSActivity
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.dcmsclient.DcmsClientMainActivity
import com.management.org.dcms.codes.dcmsclient.additem.AddItemActivity
import com.management.org.dcms.codes.dcmsclient.viewitem.ViewItemActivity
import com.management.org.dcms.codes.extensions.enableDisableView
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.TaskDetailsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.HomeViewModel
import com.management.org.dcms.databinding.ActivityMainBinding
import com.management.org.dcms.databinding.LayputHomeDashboardBinding
import com.management.org.dcms.databinding.ProfileSectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeLandingMainActivity : AppCompatActivity() {
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
    private var seeAddedHouseHoldTv:TextView?=null

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
            seeAddedHouseHoldTv=it.seeAddedHouseHoldTv
        }
        mainActivityBinding?.containerAppBar?.appBarTitleTV?.showHideView(false)
        mainActivityBinding?.containerAppBar?.toolbar?.showHideView(true)
        mainActivityBinding?.containerAppBar?.toolbar?.setTitle(R.string.app_name)

        mainActivityBinding?.containerAppBar?.toolbar?.setOnMenuItemClickListener {
            if (it.itemId == R.id.profile){
                startActivity(Intent(this, ProfileActivity::class.java))
            }else{
                performLogoutOperation()
            }
            true
        }
        mainActivityBinding?.mainDashboardContainer?.textMessageAction?.setOnClickListener {
            startTextSmsActivity()
        }

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
            if (isQuestionEnabled != null) {
               // questionActionView?.enableDisableView(isEnable = isQuestionEnabled)
            }
            if (isMessageEnabled != null) {
                messageActionView?.enableDisableView(isEnable = isMessageEnabled)
            }
            if (instruction != null) {
                instructionDetailTextView?.text = instruction
            }

        }
    }

    private fun setUpClickListener() {
        questionActionView?.setOnClickListener {
            if (taskDetailsModel != null) {
                val isQuestionEnabled = taskDetailsModel?.Task?.Questionaires
                if (isQuestionEnabled == true) {
                    startQuestionActivity()
                } else {
                    startQuestionActivity()
                   // Utility.showToastMessage("Not enabled right now")
                }
            }
            else {
                Utility.showToastMessage("Please Wait")
            }
        }
        messageActionView?.setOnClickListener {
            if (taskDetailsModel != null) {
                val isMessageEnabled = taskDetailsModel?.Task?.WAMessages
                if (isMessageEnabled == true) {
                    startMessageTemplateActivity()
                } else {
                    startMessageTemplateActivity()
                   // Utility.showToastMessage("Please Wait")
                }
            } else {
                Utility.showToastMessage("Please Wait")
            }
        }
        dataCollectionActionView?.setOnClickListener {
            startDataCollectionActivity()
        }
        seeAddedHouseHoldTv?.setOnClickListener {
            startSeeAddedHouseHoldIntent()
        }
    }

    private fun startQuestionActivity() {
        val intent = Intent(this, QuestionListingActivity::class.java)
        startActivity(intent)
    }

    private fun startMessageTemplateActivity() {
        val intent = Intent(this, MessageTemplateActivity::class.java)
        startActivity(intent)
    }

    private fun startDataCollectionActivity() {
        val intent = Intent(this, AddItemActivity::class.java)
        startActivity(intent)
    }

    private fun redirectedToSignInPage(){
        val intent =Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun startSeeAddedHouseHoldIntent(){
        val intent =Intent(this,ViewItemActivity::class.java)
        startActivity(intent)
    }
    private fun startTextSmsActivity(){
        val intent = Intent(this, SendTextSMSActivity::class.java)
        startActivity(intent)
    }

    private fun performLogoutOperation() {
        if(Utility.isUserLoggedIn()){
            progressBar?.showHideView(true)
            homeViewModel?.performLogoutOperationForServer(callbackForLogoutResponse)
        }else{
            progressBar?.showHideView(false)
            Utility.showToastMessage("Already logout")
        }
    }

    private var callbackForLogoutResponse = fun(isSuccessfullyLogout: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (isSuccessfullyLogout) {
                AuthConfigManager.logoutUser();
                Utility.showToastMessage("You have successfully Logout")
                redirectedToSignInPage()
            } else {
                Utility.showToastMessage("Something went wrong")
            }
            progressBar?.showHideView(false)
        }
    }
}