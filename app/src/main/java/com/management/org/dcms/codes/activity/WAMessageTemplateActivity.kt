package com.management.org.dcms.codes.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.adapter.ContactsListAdapter
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.ContactsMainModel
import com.management.org.dcms.codes.models.ContactsModel
import com.management.org.dcms.codes.models.WAMessageTemplateModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.MessageTemplateViewModel
import com.management.org.dcms.databinding.ActivityMessageTemplateBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder


@AndroidEntryPoint
class MessageTemplateActivity : BaseActivity() {
    private val messageTemplateViewModel: MessageTemplateViewModel? by viewModels()

    private var messageTemplateTV: TextView? = null
    private var mainActivityBinding: ActivityMessageTemplateBinding? = null
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
    private var sendToAllBtn: View? = null

    private var navIcon: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_message_template)
        setUpViews()
        setUpObservers()
        firstTimeOnCreate = true
        navIcon?.showHideView(true)
        navIcon?.setOnClickListener {
            finish()
        }
        sendToAllBtn?.setOnClickListener {
            if (messageTemplateString != null) {
                openWhatsAppForSendToAll()
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
            sendToAllBtn = it.sendWAMessageToAllBtn
        }
        contactsRecyclerView?.adapter = contactsListAdapter
    }

    private fun setUpObservers() {
        messageTemplateViewModel?.apply {
            wAMessageTemplateLiveData.observe(this@MessageTemplateActivity) { response ->
                if (response != null) {
                    parseNetworkResponseForMessageTemplate(response)
                }
            }
            contactsListLiveData.observe(this@MessageTemplateActivity) { response ->
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
        if (!successResponse.WAMessage.Template.isNullOrEmpty()) {
            messageBodyTextView?.text = successResponse.WAMessage.Template
            messageTemplateString = successResponse.WAMessage.Template
        }
       else if (!successResponse.WAMessage.MediaURL.isNullOrEmpty()) {
            messageBodyTextView?.text = successResponse.WAMessage.MediaURL
            messageTemplateString = successResponse.WAMessage.MediaURL
        }
        templateId = successResponse.WAMessage.Id
    }

    private var callback = fun(contactsMainModel: ContactsModel) {
        if (messageTemplateString != null && templateId != null && templateId != -1) {
            messageTemplateViewModel?.sentWAReportToServer(hhId = contactsMainModel.HHId, templateId = templateId!!, waNum = contactsMainModel.WANo)
            val waMobNumber = contactsMainModel.WANo
            sendToWhatsApp(waMobNumber, messageTemplateString!!)
        } else {
            Utility.showToastMessage(LanguageManager.getStringInfo(R.string.please_wait_message_template_not_received))
        }
    }

    private fun sendToWhatsApp(waMobNumber: String, messageTemplateString: String) {
        try {
            val mobWithCountryCode = countryCodeIndia + waMobNumber
            val intent = Intent(Intent.ACTION_VIEW)
            val message: String = URLEncoder.encode(messageTemplateString, "utf-8")
            intent.data = Uri.parse("${BASE_URL_FOR_WHATSAPP}send?phone=$mobWithCountryCode&text=$message")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!firstTimeOnCreate) {
            refreshListAfterSend()
        }
        firstTimeOnCreate = false
    }

    private fun openWhatsAppForSendToAll() {
        val pm = packageManager
        try {
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "text/plain"
            val text = messageTemplateString
            val info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
            waIntent.setPackage("com.whatsapp")
            waIntent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(waIntent, "Share with"))
        } catch (e: NameNotFoundException) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.localizedMessage
        }
    }
}

const val countryCodeIndia: String = "+91"
const val BASE_URL_FOR_WHATSAPP: String = "http://api.whatsapp.com/"
const val WHATSAPP_PACKAGE = "com.whatsapp"