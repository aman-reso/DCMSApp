package com.management.org.dcms.codes.activity

import android.content.*
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.adapter.ContactsListAdapter
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.ContactsMainModel
import com.management.org.dcms.codes.models.ContactsModel
import com.management.org.dcms.codes.models.TextMessageTemplateModel
import com.management.org.dcms.codes.models.WAMessageTemplateModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.MessageTemplateViewModel
import com.management.org.dcms.databinding.ActivitySendTextSmsactivityBinding
import dagger.hilt.android.AndroidEntryPoint

const val SENT = "SMS_SENT"
const val DELIVERED = "SMS_DELIVERED"

@AndroidEntryPoint
class SendTextSMSActivity : BaseActivity() {
    private val messageTemplateViewModel: MessageTemplateViewModel? by viewModels()

    private var messageTemplateTV: TextView? = null
    private var mainActivityBinding: ActivitySendTextSmsactivityBinding? = null
    private var contactsRecyclerView: RecyclerView? = null
    private val contactsListAdapter: ContactsListAdapter by lazy { ContactsListAdapter(callback = callback) }
    private var messageTemplateString: String? = null
    private var dummyImageView: ImageView? = null
    private var messageBodyTextView: TextView? = null
    private var templateId: Int? = -1
    private var progressBar: ProgressBar? = null
    private var themeId: Int? = -1
    private var campaignId: Int? = -1
    var firstTimeOnCreate = false
    var contactList: ArrayList<ContactsModel> = ArrayList()

    private var navIcon: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_send_text_smsactivity)
        setUpViews()
        setUpObservers()
        firstTimeOnCreate = true
        navIcon?.showHideView(true)
        navIcon?.setOnClickListener {
            finish()
        }
        mainActivityBinding?.sentTextMessageToAll?.setOnClickListener {
            if (!contactList.isNullOrEmpty() && messageTemplateString!=null) {
                contactList.forEach {
                    sendSms(it.MobileNo)
                }
                mainActivityBinding?.sentTextMessageToAll?.showHideView(false)
                Utility.showToastMessage(getString(R.string.message_sent))
            }
        }
    }

    private fun setUpViews() {
        mainActivityBinding?.also {
            messageTemplateTV = it.MessageTemplateTV
            contactsRecyclerView = it.contactsRecyclerView
            dummyImageView = it.dummyImageView
            messageBodyTextView = it.messageTemplateDescTextView
            progressBar = it.progressBar
            navIcon = it.containerAppBar?.icNavBackIcon
        }
        contactsRecyclerView?.adapter = contactsListAdapter
    }

    private fun setUpObservers() {
        messageTemplateViewModel?.apply {
            textMessageTemplateModel.observe(this@SendTextSMSActivity) { response ->
                if (response != null) {
                    parseNetworkResponseForMessageTemplate(response)
                }
            }
            contactsListLiveData.observe(this@SendTextSMSActivity) { response ->
                if (response != null) {
                    progressBar?.showHideView(false)
                    parseNetworkResponseForContactList(response)
                }
            }
        }
        progressBar?.showHideView(true)
        messageTemplateViewModel?.getTextSMSTemplate()
    }

    private fun parseNetworkResponseForContactList(response: GlobalNetResponse<ContactsMainModel>) {
        when (response) {
            is GlobalNetResponse.Success -> {
                val successResponse: ContactsMainModel? = response.value
                if (successResponse?.contactList != null) {
                    mainActivityBinding?.sentTextMessageToAll?.showHideView(true)
                    contactList.clear()
                    contactList.addAll(successResponse.contactList!!)
                    contactsListAdapter.submitData(successResponse.contactList!!)
                }
            }
            is GlobalNetResponse.NetworkFailure -> {

            }
            else -> {
                //nothing to do just show a toast message
            }
        }
    }

    private fun parseNetworkResponseForMessageTemplate(response: GlobalNetResponse<TextMessageTemplateModel>) {
        when (response) {
            is GlobalNetResponse.Success -> {
                val successResponse: TextMessageTemplateModel = response.value
                setMessageIntoViews(successResponse)
                messageTemplateViewModel?.getContactsListForMessage(successResponse.TextMessage?.ThemeId, successResponse.TextMessage?.CampaignId)
            }
            is GlobalNetResponse.NetworkFailure -> {

            }
        }
    }

    private fun refreshListAfterSend() {
        if (themeId != null && themeId != -1 && campaignId != null && campaignId != -1) {
            messageTemplateViewModel?.getContactsListForMessage(themeId!!, campaignId = campaignId!!)
        }
    }

    private fun setMessageIntoViews(successResponse: TextMessageTemplateModel) {
        messageBodyTextView?.text = successResponse.TextMessage?.Template
        messageTemplateString = successResponse.TextMessage?.Template
        templateId = successResponse.TextMessage?.Id
    }

    private var callback = fun(contactsMainModel: ContactsModel) {
        if (messageTemplateString != null && templateId != null && templateId != -1) {
            messageTemplateViewModel?.sentTextSMSReportToServer(hhId = contactsMainModel.HHId, templateId = templateId!!, waNum = contactsMainModel.WANo)
            val waMobNumber = contactsMainModel.WANo
            Utility.showToastMessage(getString(R.string.message_sent))
            sendSms(waMobNumber)
        } else {
            Utility.showToastMessage(getString(R.string.please_wait_message_template_not_received))
        }
    }


    override fun onResume() {
        super.onResume()
        if (!firstTimeOnCreate) {
            refreshListAfterSend()
        }
        firstTimeOnCreate = false
    }

    private fun sendSms(phoneNumber: String) {
        if (messageTemplateString!=null) {
            val sms: SmsManager = SmsManager.getDefault()
            sms.sendTextMessage(phoneNumber, null, messageTemplateString, null, null)
        }
    }
}
