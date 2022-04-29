package com.management.org.dcms.codes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.activity.Q_REPORT_INTENT
import com.management.org.dcms.codes.activity.TEXT_REPORT_INTENT
import com.management.org.dcms.codes.activity.WA_REPORT_INTENT
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.QReportDetailModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.DcmsNetworkCallRepository
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportListViewModel @Inject constructor(var repository: DcmsNetworkCallRepository) : ViewModel() {

    fun getQSentDetails(fromDate: String, toDate: String, reqForWhich: String, callback: (GlobalNetResponse<*>) -> Unit) {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken = AuthConfigManager.getAuthToken()
                when (reqForWhich) {
                    WA_REPORT_INTENT -> {
                        val networkResponse = repository.getWASentDetails(authToken, fromDate, toDate)
                        callback.invoke(networkResponse)
                    }
                    Q_REPORT_INTENT -> {
                        val networkResponse = repository.getQSentDetails(authToken, fromDate, toDate)
                        callback.invoke(networkResponse)
                    }
                    TEXT_REPORT_INTENT -> {
                        val networkResponse = repository.getTextSentDetails(authToken, fromDate, toDate)
                        callback.invoke(networkResponse)
                    }
                }
            }
        }
    }
}