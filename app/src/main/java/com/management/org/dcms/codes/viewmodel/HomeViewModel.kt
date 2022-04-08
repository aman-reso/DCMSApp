package com.management.org.dcms.codes.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.TaskDetailsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.LoginRepository
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(var loginRepository: LoginRepository) : ViewModel() {
    var taskDetailLiveData: MutableLiveData<GlobalNetResponse<TaskDetailsModel>?> = MutableLiveData()

    internal fun getTaskDetails() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken: String? = AuthConfigManager.getAuthToken()
                Log.d("asdfghjkl", "getTaskDetails: $authToken")
                var response = authToken?.let { loginRepository.getTaskDetail(it) }
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
                    when (val response = loginRepository.logoutUserFromServer(authToken = authToken)) {
                        is GlobalNetResponse.Success -> {
                            val successResponseValue = response.value
                            if (successResponseValue.Status == 200) {
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