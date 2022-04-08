package com.management.org.dcms.codes.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
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
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.net.URLEncoder


@AndroidEntryPoint
class MessageTemplateActivity : AppCompatActivity() {
    private val messageTemplateViewModel: MessageTemplateViewModel? by viewModels()

    private var messageTemplateTV: TextView? = null
    private var mainActivityBinding: ActivityMessageTemplateBinding? = null
    private var contactsRecyclerView: RecyclerView? = null
    private val contactsListAdapter: ContactsListAdapter by lazy { ContactsListAdapter(callback = callback) }
    private var messageTemplateString: String? = null
    private var messageBodyTextView: TextView? = null

    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_message_template)
        setUpViews()
        setUpObservers()
    }

    private fun setUpViews() {
        mainActivityBinding?.also {
            messageTemplateTV = it.MessageTemplateTV
            contactsRecyclerView = it.contactsRecyclerView
            messageBodyTextView = it.messageTemplateDescTextView
            progressBar = it.progressBar
        }
        contactsRecyclerView?.adapter = contactsListAdapter

        mainActivityBinding?.containerAppBar?.toolbar?.visibility = View.GONE
        mainActivityBinding?.containerAppBar?.icNavBackIcon?.visibility = View.VISIBLE
        mainActivityBinding?.containerAppBar?.appBarTitleTV?.visibility = View.VISIBLE

        mainActivityBinding?.containerAppBar?.icNavBackIcon?.setOnClickListener { onBackPressed() }

        mainActivityBinding?.sendToWaGroupBtn?.setOnClickListener {
            try {
                val message = messageBodyTextView?.text.toString()
                val whatsAppIntent = Intent()
                whatsAppIntent.data =
                    Uri.parse("whatsapp://send?text=${URLEncoder
                        .encode(message,"UTF-8")}")
                whatsAppIntent.action = Intent.ACTION_VIEW
                startActivity(whatsAppIntent)
            }catch (e: ActivityNotFoundException){
                Toast.makeText(this, "You don't have whatsapp installed!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpObservers() {
        messageTemplateViewModel?.apply {
            messageTemplateLiveData.observe(this@MessageTemplateActivity) { response ->
                if (response != null) {
                    progressBar?.showHideView(false)
                    parseNetworkResponseForMessageTemplate(response)
                }
            }
            contactsListLiveData.observe(this@MessageTemplateActivity) { response ->
                if (response != null) {
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
                val successResponse: ContactsMainModel = response.value
                contactsListAdapter.submitData(successResponse.contactList)
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
            }
            is GlobalNetResponse.NetworkFailure -> {

            }
        }
    }

    private fun setMessageIntoViews(successResponse: WAMessageTemplateModel) {
        messageBodyTextView?.text = successResponse.WAMessage.Template
        messageTemplateString = successResponse.WAMessage.Template
    }

    private var callback = fun(contactsMainModel: ContactsModel) {
        if (messageTemplateString != null) {
            val waMobNumber = contactsMainModel.WANo
            sendToWhatsApp(waMobNumber, messageTemplateString!!)
        } else {
            Utility.showToastMessage("Please wait Message Template Not Received")
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
}

const val countryCodeIndia: String = "+91"
const val BASE_URL_FOR_WHATSAPP: String = "http://api.whatsapp.com/"
const val WHATSAPP_PACKAGE = "com.whatsapp"