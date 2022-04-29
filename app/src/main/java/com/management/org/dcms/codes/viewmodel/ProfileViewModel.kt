package com.management.org.dcms.codes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.LanguageDataClass
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.DcmsNetworkCallRepository
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(var dcmsNetworkCallRepository: DcmsNetworkCallRepository) : ViewModel() {
    var profileInfoLiveData: MutableLiveData<GlobalNetResponse<*>> = MutableLiveData()
    var languageCodeLiveData= MutableLiveData<String>()
    internal fun getProfileInfo() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken = AuthConfigManager.getAuthToken()
                val response = dcmsNetworkCallRepository.getProfileDetails(authToken)
                profileInfoLiveData.postValue(response)
            }
        } else {
            profileInfoLiveData.postValue(null)
        }
    }

    internal fun changeProfilePassword(oldPassword: String, newPassword: String, confirmPassword: String, callback: (String?, Int) -> Unit) {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken = AuthConfigManager.getAuthToken()
                val networkResponse = dcmsNetworkCallRepository.changeUserPassword(
                    authToken, oldPassword = oldPassword, newPassword = newPassword,
                    confirmPassword = confirmPassword
                )
                when (networkResponse) {
                    is GlobalNetResponse.Success -> {
                        val successResponse = networkResponse.value
                        if (successResponse.has("Message")) {
                            if (successResponse.has("Status")) {
                                val message = successResponse["Message"].asString
                                val status = successResponse["Status"].asInt
                                callback.invoke(message, status)
                            }
                        }
                    }
                    is GlobalNetResponse.NetworkFailure -> {
                        val message = networkResponse.error
                        callback.invoke(message, -1)
                    }
                }
            }
        } else {
            profileInfoLiveData.postValue(null)
        }
    }

    fun forgotUserPassword(mobileNo: String, callback: (String?, Int) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        when (val networkResponse = dcmsNetworkCallRepository.forgotUserPassword(mobileNo = mobileNo)) {
            is GlobalNetResponse.Success -> {
                val successResponse = networkResponse.value
                if (successResponse.has("Message")) {
                    if (successResponse.has("Status")) {
                        val message = successResponse["Message"].asString
                        val status = successResponse["Status"].asInt
                        callback.invoke(message, status)
                        return@launch
                    }
                }
            }
            is GlobalNetResponse.NetworkFailure -> {
                val message = networkResponse.error
                callback.invoke(message, -1)
            }
        }
    }
    internal fun getDefaultLanguageSelectedByUser(){
        viewModelScope.launch(Dispatchers.IO) {
            val langCode= LanguageManager.getLanguageCode()
            languageCodeLiveData.postValue(langCode)
        }
    }
//{"Message":"New password has been sent to your Registered Email-Id, Kindly check","Status":1,"Profile":null}
}