package com.management.org.dcms.codes

import android.content.Intent
import android.graphics.Paint
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
import com.management.org.dcms.codes.models.CampaignListModel
import com.management.org.dcms.codes.models.CampaignModel
import com.management.org.dcms.codes.models.TaskDetailsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.LanguageManager.getStringInfo
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.HomeViewModel
import com.management.org.dcms.databinding.ActivityMainBinding
import com.management.org.dcms.databinding.LayputHomeDashboardBinding
import com.management.org.dcms.databinding.ProfileSectionBinding
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerView
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
    private var wAMessageActionView: View? = null
    private var registrationActionView: View? = null
    private var viewEntriesView: View? = null
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
    private var callActionBtn: View? = null

    //added skyDovesPowerSpinner
    private val campaignSpinner: PowerSpinnerView? by lazy { mainActivityBinding?.campaignPowerSpinner }

    private var registrationImageURL: String = "http://dcms.dmi.ac.in/content/dist/img/Registration.png"
    private var surveyImageURL: String = "http://dcms.dmi.ac.in/content/dist/img/questionairs.png"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpViews()
        setUpObserver()
        setUpClickListener()

        val textView = findViewById<View>(R.id.instructionTitleTextView) as TextView
        textView.paintFlags = Paint.UNDERLINE_TEXT_FLAG

    }

    private fun setUpViews() {
        mainActivityBinding?.let {
            dashboardBinding = it.mainDashboardContainer
            wAMessageActionView = dashboardBinding?.messageMainAction
            registrationActionView = dashboardBinding?.actionBtnForStartReg
            questionActionView = dashboardBinding?.actionBtnForStartSurvey
            viewEntriesView = dashboardBinding?.actionBtnForViewEntries
            progressBar = it.progressBar
            instructionTV = it.instructionTitleTextView
            instructionDetailTextView = it.instructionDetailTextView
            logoutTextView = profileSectionBinding?.logoutSection
            mobNumTextView = profileSectionBinding?.profileContactNumTV
            nameTextView = profileSectionBinding?.profileNameTV
            textMessageActionBtn = dashboardBinding?.textMessageAction
            callActionBtn = dashboardBinding?.CallBtn
        }
        mainActivityBinding?.containerAppBar?.appBarTitleTV?.showHideView(false)
        mainActivityBinding?.containerAppBar?.toolbar?.showHideView(true)
        mainActivityBinding?.containerAppBar?.toolbar?.setTitle(R.string.app_name)
        mainActivityBinding?.containerAppBar?.toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.changeLanguage -> {
                    startActivity(Intent(this, ChangeLanguageActivity::class.java))
                    finish()
                }
                else -> {
                    performLogoutOperation()
                }
            }
            true
        }
        if (dashboardBinding?.imageViewForRegistration != null) {
            Picasso.get().load(registrationImageURL)
                .into(dashboardBinding?.imageViewForRegistration)
        }
        if (dashboardBinding?.imageViewForStartSurvey != null) {
            Picasso.get().load(surveyImageURL).into(dashboardBinding?.imageViewForStartSurvey)
        }
    }


    private fun setUpObserver() {
        homeViewModel?.getCampaignList()
        homeViewModel?.getTaskDetails()
        progressBar?.showHideView(true)
        homeViewModel?.apply {
            taskDetailLiveData.observe(this@HomeLandingMainActivity) {
                progressBar?.showHideView(false)
                parseNetworkResponse(it)
            }
            campaignListLiveData.observe(this@HomeLandingMainActivity) {
                progressBar?.showHideView(false)
                parseNetworkResponseForCampaignList(it)
            }
        }

    }

    private fun parseNetworkResponseForCampaignList(response: GlobalNetResponse<CampaignListModel>?) {
        when (response) {
            is GlobalNetResponse.NetworkFailure -> {

            }
            is GlobalNetResponse.Success -> {
                val successResponse = response.value
                if (successResponse.campItemList == null) {
                    Utility.showToastMessage("Something went wrong")
                    return
                }
                bindDataWithSpinner(successResponse.campItemList!!)
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
                instructionDetailTextView?.text = taskDetailsModel?.Task?.Instructions
                if (successResponse.Task!=null && successResponse.Task?.ThemeId!=null) {
                    DcmsApplication.setThemeId(successResponse.Task?.ThemeId?.toString()!!)
                }
            }
            else -> {

            }
        }
    }


    private fun setUpClickListener() {
        //report
        questionActionView?.setOnClickListener {
            if (validateCampId()) {
                handleQuestionaireIntent()
            } else {
                Utility.showToastMessage("Please Select Campaign")
            }
        }
        //waMessage
        wAMessageActionView?.setOnClickListener {
            handleWAMessageIntent()
        }
        //viewEntries
        viewEntriesView?.setOnClickListener {
            viewEntriesClass()
        }
        registrationActionView?.setOnClickListener {
            handleDataCollectionIntent()
        }
        textMessageActionBtn?.setOnClickListener {
            if (validateCampId()) {
                handleTextMessageIntent()
            } else {
                Utility.showToastMessage("Please Select Campaign")
            }
        }
        //call
        callActionBtn?.setOnClickListener {
            if (validateCampId()) {
                handleCallIntent()
            } else {
                Utility.showToastMessage("Please Select Campaign")
            }
        }

        seeAddedHouseHoldTv?.setOnClickListener {
            startSeeAddedHouseHoldIntent()
        }
        campaignSpinner?.setOnSpinnerItemSelectedListener(OnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (newIndex != -1) {
                val id = homeViewModel?.campList?.get(newIndex)?.campId
                if (id != null) {
                    DcmsApplication.setCampId(id)
                }
            }
        })

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

    private fun startCallActivity() {
        val intent = Intent(this, CallDetailsActivity::class.java)
        startActivity(intent)
    }

    private fun handleDataCollectionIntent() {
        if (taskDetailsModel != null) {
            val isMessageEnabled = taskDetailsModel?.Task?.DataCollection
            if (isMessageEnabled == true) {
                startDataCollectionActivity()
            } else {
                Utility.showToastMessage(getStringInfo(R.string.not_enabled_right_now))
            }
        } else {
            Utility.showToastMessage(getStringInfo(R.string.please_wait))
        }
    }

    private fun handleTextMessageIntent() {
        if (taskDetailsModel != null) {
            val isMessageEnabled = taskDetailsModel?.Task?.TextMessage
            if (isMessageEnabled == true) {
                startTextSmsActivity()
            } else {
                Utility.showToastMessage(getStringInfo(R.string.not_enabled_right_now))
            }
        } else {
            Utility.showToastMessage(getStringInfo(R.string.please_wait))
        }
    }

    private fun handleCallIntent() {
        if (taskDetailsModel != null) {
            val isMessageEnabled = taskDetailsModel?.Task?.CallDetail
            if (isMessageEnabled == true) {
                startCallActivity()
            } else {
                Utility.showToastMessage(getStringInfo(R.string.not_enabled_right_now))
            }
        } else {
            Utility.showToastMessage(getStringInfo(R.string.please_wait))
        }
    }

    private fun handleWAMessageIntent() {
        if (taskDetailsModel != null) {
            val isMessageEnabled = taskDetailsModel?.Task?.WAMessages
            if (isMessageEnabled == true) {
                startMessageTemplateActivity()
            } else {
                Utility.showToastMessage(getStringInfo(R.string.not_enabled_right_now))
            }
        } else {
            Utility.showToastMessage(getStringInfo(R.string.please_wait))
        }
    }

    private fun handleQuestionaireIntent() {
        if (taskDetailsModel != null) {
            val isQuestionEnabled = taskDetailsModel?.Task?.Questionaires
            if (isQuestionEnabled == true) {
                startQuestionActivity()
            } else {
                Utility.showToastMessage(getStringInfo(R.string.not_enabled_right_now))
            }
        } else {
            Utility.showToastMessage(getStringInfo(R.string.please_wait))
        }
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
                Utility.showToastMessage(getStringInfo(R.string.log_out_message))
                redirectedToSignInPage()
            } else {
                Utility.showToastMessage(getStringInfo(R.string.something_went_wrong))
            }
            progressBar?.showHideView(false)
        }
    }

    private fun viewEntriesClass() {
        val intent = Intent(this, ViewEntriesActivity::class.java)
        startActivity(intent)
    }

    private fun bindDataWithSpinner(campItemList: ArrayList<CampaignModel>) {
        homeViewModel?.campList?.clear()
        homeViewModel?.campList?.addAll(campItemList)
        campaignSpinner?.setItems(campItemList.map { it.campName })
        if (campItemList.size == 1) {
            DcmsApplication.setCampId(campItemList[0].campId!!)
            campaignSpinner?.selectItemByIndex(0)
        }

    }

    private fun validateCampId(): Boolean {
        val campId = DcmsApplication.getCampId()
        if (campId == "-1") {
            return false
        }
        return true
    }

}