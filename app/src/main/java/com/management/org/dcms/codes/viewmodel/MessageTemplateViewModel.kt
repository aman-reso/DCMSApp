package com.management.org.dcms.codes.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.ContactsMainModel
import com.management.org.dcms.codes.models.QContactsMainModel
import com.management.org.dcms.codes.models.WAMessageTemplateModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.LoginRepository
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageTemplateViewModel @Inject constructor(var loginRepository: LoginRepository) : ViewModel() {
    var messageTemplateLiveData: MutableLiveData<GlobalNetResponse<WAMessageTemplateModel>> = MutableLiveData()
    var contactsListLiveData: MutableLiveData<GlobalNetResponse<ContactsMainModel>> = MutableLiveData()
    var qContactListLiveData: MutableLiveData<GlobalNetResponse<QContactsMainModel>> = MutableLiveData()

    //use mediator live data here
    internal fun getWAMessageTemplate() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken = AuthConfigManager.getAuthToken()
                async {
                    val response = loginRepository.getWAMessageTemplate(authToken!!)
                    messageTemplateLiveData.postValue(response)
                }
            }
        }
    }

    fun getContactsListForMessage(themeId: Int, campaignId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val authToken = AuthConfigManager.getAuthToken()
            val response = loginRepository.getContactsList(authToken!!, themeId, campaignId)
            contactsListLiveData.postValue(response)
        }
    }

    fun sentWAReportToServer(hhId: Int, templateId: Int, waNum: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val authToken = AuthConfigManager.getAuthToken()
            println("authToken==>+$authToken")
            if (authToken != null) {
                when (val serverResponse = loginRepository.sentWAReport(authToken = authToken, hhId = hhId, templateId = templateId, waNum = waNum)) {
                    is GlobalNetResponse.Success -> {
                        println("serverResponse$serverResponse")
                    }
                    is GlobalNetResponse.NetworkFailure -> {

                    }
                }
            }
        }
    }

    fun getContactsListForQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            val authToken = AuthConfigManager.getAuthToken()
            if (authToken != null) {
                val serverResponse = loginRepository.getContactsListForQuestion(authToken = authToken, themeId = 1, campaignId = 1)
                qContactListLiveData.postValue(serverResponse)
            }
        }
    }
}
