package com.management.org.dcms.codes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.management.org.dcms.R
import com.management.org.dcms.codes.activity.MessageTemplateActivity
import com.management.org.dcms.codes.extensions.enableDisableView
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.Task
import com.management.org.dcms.codes.models.TaskDetailsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.HomeViewModel
import com.management.org.dcms.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeLandingMainActivity : AppCompatActivity() {
    private val homeViewModel: HomeViewModel? by viewModels()

    private var mainActivityBinding: ActivityMainBinding? = null
    private var messageActionView: View? = null
    private var questionActionView: View? = null
    private var progressBar: ProgressBar? = null
    private var taskDetailsModel: TaskDetailsModel? = null
    private var instructionTV: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpViews()
        setUpObserver()
        setUpClickListener()
    }

    private fun setUpViews() {
        mainActivityBinding?.let {
            messageActionView = it.messageMainAction
            questionActionView = it.questionMainAction
            progressBar = it.progressBar
            instructionTV = it.instructionTV
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
            var insturction = taskDetailsModel.Task?.Instructions
            if (isQuestionEnabled != null) {
                questionActionView?.enableDisableView(isEnable = isQuestionEnabled)
            }
            if (isMessageEnabled != null) {
                messageActionView?.enableDisableView(isEnable = isMessageEnabled)
            }
            if (insturction != null) {
                instructionTV?.text = insturction
            }

        }
    }

    private fun setUpClickListener() {
        questionActionView?.setOnClickListener {
            if (taskDetailsModel != null) {
                val isQuestionEnabled = taskDetailsModel?.Task?.Questionaires
                if (isQuestionEnabled == true) {

                } else {
                    Utility.showToastMessage("Please Wait")
                }
            } else {
                Utility.showToastMessage("Please Wait")
            }
        }
        messageActionView?.setOnClickListener {
            if (taskDetailsModel != null) {
                val isMessageEnabled = taskDetailsModel?.Task?.WAMessages
                if (isMessageEnabled == true) {
                    startMessageTemplateActivity()
                } else {
                    Utility.showToastMessage("Please Wait")
                }
            } else {
                Utility.showToastMessage("Please Wait")
            }
        }
    }

    private fun startMessageTemplateActivity() {
        val intent = Intent(this, MessageTemplateActivity::class.java)
        startActivity(intent)
    }
}