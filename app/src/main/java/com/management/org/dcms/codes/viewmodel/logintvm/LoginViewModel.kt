package com.management.org.dcms.codes.viewmodel.logintvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.LoginResponseData
import com.management.org.dcms.codes.models.TaskDetailsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.LoginRepository
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.utility.Utility.showToastMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(var loginRepository: LoginRepository) : ViewModel() {
    var authLiveData: MutableLiveData<GlobalNetResponse<LoginResponseData>> = MutableLiveData()

    fun submitLoginData(inputPhone: String, inputPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = loginRepository.submitRequestForLogin(inputPhone, inputPassword)
            authLiveData.postValue(response)
        }
    }
}