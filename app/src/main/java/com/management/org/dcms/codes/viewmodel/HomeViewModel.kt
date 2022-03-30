package com.management.org.dcms.codes.viewmodel

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

    fun getTaskDetails() {
        if (Utility.isUserLoggedIn()) {
            viewModelScope.launch(Dispatchers.IO) {
                val authToken: String? = AuthConfigManager.getAuthToken()
                var response= loginRepository.getTaskDetail(authToken!!)
                taskDetailLiveData.postValue(response)
            }
        } else {
            taskDetailLiveData.postValue(null)
        }
    }
}