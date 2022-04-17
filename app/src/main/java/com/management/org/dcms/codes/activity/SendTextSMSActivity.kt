package com.management.org.dcms.codes.activity

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.adapter.ContactsListAdapter
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.ContactsMainModel
import com.management.org.dcms.codes.models.ContactsModel
import com.management.org.dcms.codes.models.WAMessageTemplateModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.MessageTemplateViewModel
import com.management.org.dcms.databinding.ActivityMessageTemplateBinding
import com.management.org.dcms.databinding.ActivitySendTextSmsactivityBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

const val SENT = "SMS_SENT"
const val DELIVERED = "SMS_DELIVERED"

@AndroidEntryPoint
class SendTextSMSActivity : AppCompatActivity() {
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
                Utility.showToastMessage("Message send")
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
            messageTemplateLiveData.observe(this@SendTextSMSActivity) { response ->
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
        messageTemplateViewModel?.getWAMessageTemplate()
    }

    private fun parseNetworkResponseForContactList(response: GlobalNetResponse<ContactsMainModel>) {
        when (response) {
            is GlobalNetResponse.Success -> {
                val successResponse: ContactsMainModel? = response.value
                println("successResponse-->$successResponse")
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

    private fun parseNetworkResponseForMessageTemplate(response: GlobalNetResponse<WAMessageTemplateModel>) {
        when (response) {
            is GlobalNetResponse.Success -> {
                val successResponse: WAMessageTemplateModel = response.value
                setMessageIntoViews(successResponse)
                messageTemplateViewModel?.getContactsListForMessage(successResponse.WAMessage.ThemeId, successResponse.WAMessage.CampaignId)
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

    private fun setMessageIntoViews(successResponse: WAMessageTemplateModel) {
        messageBodyTextView?.text = successResponse.WAMessage.Template
        messageTemplateString = successResponse.WAMessage.Template
        templateId = successResponse.WAMessage.Id
    }

    private var callback = fun(contactsMainModel: ContactsModel) {
        if (messageTemplateString != null && templateId != null && templateId != -1) {
            messageTemplateViewModel?.sentWAReportToServer(hhId = contactsMainModel.HHId, templateId = templateId!!, waNum = contactsMainModel.WANo)
            val waMobNumber = contactsMainModel.WANo
            Utility.showToastMessage("Message Sent")
            sendSms(waMobNumber)
        } else {
            Utility.showToastMessage("Please wait Message Template Not Received")
        }
    }


    override fun onResume() {
        super.onResume()
        if (!firstTimeOnCreate) {
            refreshListAfterSend()
        }
        firstTimeOnCreate = false
    }

    private fun sendTextMessageToAll() {

    }

    private fun multipleSMS(phoneNumber: String, message: String) {
//        val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), 0)
//        val deliveredPI = PendingIntent.getBroadcast(this, 0, Intent(DELIVERED), 0)
//        // ---when the SMS has been sent---
//        registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context?, arg1: Intent?) {
//                when (resultCode) {
//                    Activity.RESULT_OK -> {
//                        val values = ContentValues()
//                        var i = 0
//                        while (i < MobNumber.size() - 1) {
//                            values.put("address", MobNumber.get(i).toString())
//                            // txtPhoneNo.getText().toString());
//                            values.put("body", MessageText.getText().toString())
//                            i++
//                        }
//                        contentResolver.insert(
//                            Uri.parse("content://sms/sent"), values
//                        )
//                        Toast.makeText(baseContext, "SMS sent", Toast.LENGTH_SHORT).show()
//                    }
//                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(baseContext, "Generic failure", Toast.LENGTH_SHORT).show()
//                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(baseContext, "No service", Toast.LENGTH_SHORT).show()
//                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(baseContext, "Null PDU", Toast.LENGTH_SHORT).show()
//                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(baseContext, "Radio off", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }, IntentFilter(SENT))
//
//        // ---when the SMS has been delivered---
//        registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context?, arg1: Intent?) {
//                when (resultCode) {
//                    Activity.RESULT_OK -> Toast.makeText(baseContext, "SMS delivered", Toast.LENGTH_SHORT).show()
//                    Activity.RESULT_CANCELED -> Toast.makeText(baseContext, "SMS not delivered", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }, IntentFilter(DELIVERED))
    }

    private fun sendSms(phoneNumber: String) {
        if (messageTemplateString!=null) {
            val sms: SmsManager = SmsManager.getDefault()
            sms.sendTextMessage(phoneNumber, null, messageTemplateString, null, null)
        }
    }
}
