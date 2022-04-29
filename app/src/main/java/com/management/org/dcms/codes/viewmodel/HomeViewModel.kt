package com.management.org.dcms.codes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.SentReportQActivityModel
import com.management.org.dcms.codes.models.TaskDetailsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.DcmsNetworkCallRepository
import com.management.org.dcms.codes.utility.AndroidDeviceUtils
import com.management.org.dcms.codes.utility.GeneralUtils
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(var dcmsNetworkCallRepository: DcmsNetworkCallRepository) : ViewModel() {
    var taskDetailLiveData: MutableLiveData<GlobalNetResponse<TaskDetailsModel>?> = MutableLiveData()

    internal fun getTaskDetails() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken: String? = AuthConfigManager.getAuthToken()
                var response = authToken?.let { dcmsNetworkCallRepository.getTaskDetail(it) }
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
                    when (val response = dcmsNetworkCallRepository.logoutUserFromServer(authToken = authToken)) {
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

}