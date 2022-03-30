package com.management.org.dcms.codes.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.ContactsMainModel
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

    internal fun getWAMessageTemplate() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken = AuthConfigManager.getAuthToken()
                async {
                    val response = loginRepository.getWAMessageTemplate(authToken!!)
                    messageTemplateLiveData.postValue(response)
                }
                async {
                    val response = loginRepository.getContactsList(authToken!!)
                    contactsListLiveData.postValue(response)
                }
            }
        }
    }
}