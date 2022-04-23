package com.management.org.dcms.codes.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.adapter.QContactsListAdapter
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.QContactsMainModel
import com.management.org.dcms.codes.models.QContactsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.MessageTemplateViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class ContactsListingForQuestionActivity : BaseActivity() {
    private val messageTemplateViewModel: MessageTemplateViewModel? by viewModels()
    private val contactsListAdapter: QContactsListAdapter by lazy { QContactsListAdapter(callback = callback) }
    private var qContactsRecyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_listing)
        getDataFromIntent()
        setUpViews()
        setUpObservers()
    }

    private fun getDataFromIntent() {
        try {
            if (intent != null && intent.data != null) {
                //urlToBeLoad = intent.getStringExtra(URL_TO_BE_LOAD)
            }
        } catch (e: Exception) {
            Utility.showToastMessage(getString(R.string.something_went_wrong))
            finish()
        }
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
            wAMessageTemplateLiveData.observe(this@ContactsListingForQuestionActivity) { response ->
                if (response != null) {
                    //  parseNetworkResponseForMessageTemplate(response)
                }
            }
            qContactListLiveData.observe(this@ContactsListingForQuestionActivity) { networkResponse ->
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
                Utility.showToastMessage(getString(R.string.something_went_wrong))
            }
            is GlobalNetResponse.Success -> {
                val contactsList = networkResponse.value
                if (contactsList?.qContactsList != null) {
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
        progressBar?.showHideView(true)
        messageTemplateViewModel?.sentReportForQuestionActivity(hhId = contactsMainModel.HHId, waNum = contactsMainModel.WANo) { url ->
            lifecycleScope.launch(Dispatchers.Main) {
                progressBar?.showHideView(false)
                if (url != null) {
                    val intent = Intent(this@ContactsListingForQuestionActivity, AttemptQuestionActivity::class.java)
                    intent.putExtra(HH_ID, contactsMainModel.HHId)
                    intent.putExtra(Q_ID,contactsMainModel.Id)
                    intent.putExtra(URL_TO_BE_LOAD, url)
                    startActivity(intent)
                } else {
                    Utility.showToastMessage(getString(R.string.something_went_wrong))
                }
            }
        }
    }
}