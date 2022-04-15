package com.management.org.dcms.codes.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.adapter.ContactsListAdapter
import com.management.org.dcms.codes.adapter.QContactsListAdapter
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.ContactsModel
import com.management.org.dcms.codes.models.QContactsMainModel
import com.management.org.dcms.codes.models.QContactsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.MessageTemplateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionListingActivity : AppCompatActivity() {
    private val messageTemplateViewModel: MessageTemplateViewModel? by viewModels()
    private val contactsListAdapter: QContactsListAdapter by lazy { QContactsListAdapter(callback = callback) }
    private var qContactsRecyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_listing)
        setUpViews()
        setUpObservers()
    }

    private fun setUpViews() {
        qContactsRecyclerView = findViewById(R.id.qContactsRecyclerView)
        qContactsRecyclerView?.adapter = contactsListAdapter
        progressBar = findViewById(R.id.progressBar)
        val navIcon: ImageView? = findViewById(R.id.icNavBackIcon)
        navIcon?.showHideView(true)
        navIcon?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpObservers() {
        messageTemplateViewModel?.apply {
            messageTemplateLiveData.observe(this@QuestionListingActivity) { response ->
                if (response != null) {
                    //  parseNetworkResponseForMessageTemplate(response)
                }
            }
            qContactListLiveData.observe(this@QuestionListingActivity) { networkResponse ->
                if (networkResponse != null) {
                    progressBar?.showHideView(false)
                    parseNetworkResponse(networkResponse)
                }
            }
        }
        progressBar?.showHideView(true)
        messageTemplateViewModel?.getContactsListForQuestion()
    }

    private fun parseNetworkResponse(networkResponse: GlobalNetResponse<QContactsMainModel>?) {
        progressBar?.showHideView(false)
        when (networkResponse) {
            is GlobalNetResponse.NetworkFailure -> {
                Utility.showToastMessage("Something went wrong")
            }
            is GlobalNetResponse.Success -> {
                val contactsList = networkResponse.value
                if (contactsList?.qContactsList != null) {
                    System.out.println("list of contacts-->" + contactsList?.qContactsList)
                    contactsListAdapter.submitData(contactsList = contactsList?.qContactsList!!)
                }
            }
        }
    }

    private fun refreshListAfterSend() {
        // if (themeId != null && themeId != -1 && campaignId != null && campaignId != -1) {
        //messageTemplateViewModel?.getContactsListForMessage(1, campaignId =1)
        //}
    }

    private var callback = fun(contactsMainModel: QContactsModel) {
        messageTemplateViewModel?.sentWAReportToServer(hhId = contactsMainModel.HHId, templateId = 1, waNum = contactsMainModel.MobileNo)

//        if (messageTemplateString != null && templateId != null && templateId != -1) {
//            messageTemplateViewModel?.sentWAReportToServer(hhId = contactsMainModel.HHId, templateId = templateId!!, waNum = contactsMainModel.WANo)
//            val waMobNumber = contactsMainModel.WANo
//            sendToWhatsApp(waMobNumber, messageTemplateString!!)
//        } else {
//            Utility.showToastMessage("Please wait Message Template Not Received")
//        }
        val intent = Intent(this, AttemptQuestionActivity::class.java)
        startActivity(intent)
    }
}