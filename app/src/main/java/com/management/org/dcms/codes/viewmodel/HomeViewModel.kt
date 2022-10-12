package com.management.org.dcms.codes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.CallSentReportResponse
import com.management.org.dcms.codes.models.CampaignListModel
import com.management.org.dcms.codes.models.CampaignModel
import com.management.org.dcms.codes.models.TaskDetailsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.DcmsNetworkCallRepository
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(var dcmsNetworkCallRepository: DcmsNetworkCallRepository) :
    ViewModel() {
    internal var taskDetailLiveData: MutableLiveData<GlobalNetResponse<TaskDetailsModel>?> =
        MutableLiveData()
    internal var callLogSentReportLiveData: MutableLiveData<GlobalNetResponse<CallSentReportResponse>?> =
        MutableLiveData()
    internal var campaignListLiveData: MutableLiveData<GlobalNetResponse<CampaignListModel>?> =
        MutableLiveData()
    internal var campList:ArrayList<CampaignModel> = ArrayList()

    internal fun getTaskDetails() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken: String = AuthConfigManager.getAuthToken()
                val response = authToken.let { dcmsNetworkCallRepository.getTaskDetail(it) }
                taskDetailLiveData.postValue(response)
            }
        } else {
            taskDetailLiveData.postValue(null)
        }
    }

    internal inline fun performLogoutOperationForServer(crossinline callbackForLogout: (Boolean) -> Unit) {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken: String? = AuthConfigManager.getAuthToken()
                if (authToken != null) {
                    when (val response =
                        dcmsNetworkCallRepository.logoutUserFromServer(authToken = authToken)) {
                        is GlobalNetResponse.Success -> {
                            val successResponseValue = response.value
                            if (successResponseValue.Status == 1) {
                                callbackForLogout.invoke(true)
                            } else {
                                callbackForLogout.invoke(false)
                            }
                        }
                        is GlobalNetResponse.NetworkFailure -> {
                            callbackForLogout.invoke(false)
                        }
                    }
                }
            }
        }
    }

    internal fun getCampaignList() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken: String = AuthConfigManager.getAuthToken()
                System.out.println("authToken-->$authToken")
                val response = dcmsNetworkCallRepository.makeApiCallForGettingCampaign(authToken)
                campaignListLiveData.postValue(response)
            }
        } else {
            campaignListLiveData.postValue(null)
        }
    }

    internal fun getCallLogsReport(fromDateString: String, toDateString: String) {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken: String? = AuthConfigManager.getAuthToken()
                if (authToken != null) {
                    val response = dcmsNetworkCallRepository.getCallLogsReport(authToken = authToken,fromDateString,toDateString)
                    callLogSentReportLiveData.postValue(response)
                }else{
                    callLogSentReportLiveData.postValue(null)
                }
            }
        }else{
            callLogSentReportLiveData.postValue(null)
        }
    }

}