package com.management.org.dcms.codes.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.*
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.DcmsNetworkCallRepository
import com.management.org.dcms.codes.utility.AndroidDeviceUtils
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MessageTemplateViewModel @Inject constructor(var dcmsNetworkCallRepository: DcmsNetworkCallRepository) : ViewModel() {
    var messageTemplateLiveData: MutableLiveData<GlobalNetResponse<WAMessageTemplateModel>> = MutableLiveData()
    var contactsListLiveData: MutableLiveData<GlobalNetResponse<ContactsMainModel>> = MutableLiveData()
    var qContactListLiveData: MutableLiveData<GlobalNetResponse<QContactsMainModel>> = MutableLiveData()

    //use mediator live data here
    internal fun getWAMessageTemplate() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken = AuthConfigManager.getAuthToken()
                async {
                    val response = dcmsNetworkCallRepository.getWAMessageTemplate(authToken!!)
                    messageTemplateLiveData.postValue(response)
                }
            }
        }
    }

    fun getContactsListForMessage(themeId: Int, campaignId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val authToken = AuthConfigManager.getAuthToken()
            val response = dcmsNetworkCallRepository.getContactsList(authToken!!, themeId, campaignId)
            contactsListLiveData.postValue(response)
        }
    }

    fun sentWAReportToServer(hhId: Int, templateId: Int, waNum: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val authToken = AuthConfigManager.getAuthToken()
            println("authToken==>+$authToken")
            if (authToken != null) {
                when (val serverResponse = dcmsNetworkCallRepository.sentWAReport(authToken = authToken, hhId = hhId, templateId = templateId, waNum = waNum)) {
                    is GlobalNetResponse.Success -> {
                        println("serverResponse$serverResponse")
                    }
                    is GlobalNetResponse.NetworkFailure -> {

                    }
                }
            }
        }
    }

    internal inline fun sentReportForQuestionActivity(hhId: Int, waNum: String, crossinline callback: (String?) -> Unit) {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken: String? = AuthConfigManager.getAuthToken()
                if (authToken != null) {
                    val sentReportQActivityModel = SentReportQActivityModel()
                    sentReportQActivityModel.Andversion = AndroidDeviceUtils.getAndroidVersion()
                    sentReportQActivityModel.DeviceId = AndroidDeviceUtils.getDeviceId()
                    sentReportQActivityModel.IPAddress = AndroidDeviceUtils.getLocalIpAddress()
                    sentReportQActivityModel.HHId = hhId
                    sentReportQActivityModel.WANo = waNum
                    sentReportQActivityModel.Lattitude = "1"
                    sentReportQActivityModel.Longitude = "2"
                    when (val response = dcmsNetworkCallRepository.sentReportQuestionActivity(sentReportQActivityModel = sentReportQActivityModel, authToken = authToken)) {
                        is GlobalNetResponse.Success -> {
                            val successResponseValue:JsonObject=response.value
                            if (successResponseValue.has("status") && successResponseValue.get("status").asInt==1) {
                                callback.invoke(successResponseValue.get("URL").asString)
                            }
                            else{
                                withContext(Dispatchers.Main){
                                    Utility.showToastMessage(successResponseValue.get("Message").asString)
                                }
                                callback.invoke(null)
                            }
                        }
                        is GlobalNetResponse.NetworkFailure -> {
                            callback.invoke(null)
                        }
                    }
                }
            }
        }
    }

    fun getContactsListForQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            val authToken = AuthConfigManager.getAuthToken()
            if (authToken != null) {
                val serverResponse = dcmsNetworkCallRepository.getContactsListForQuestion(authToken = authToken, themeId = 1, campaignId = 1)
                qContactListLiveData.postValue(serverResponse)
            }
        }
    }
}
