package com.management.org.dcms.codes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.models.LoginResponseData
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.DcmsNetworkCallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(var dcmsNetworkCallRepository: DcmsNetworkCallRepository) : ViewModel() {
    internal var authLiveData: MutableLiveData<GlobalNetResponse<LoginResponseData>> = MutableLiveData()

    internal fun submitLoginData(inputPhone: String, inputPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = dcmsNetworkCallRepository.submitRequestForLogin(inputPhone, inputPassword)
            authLiveData.postValue(response)
        }
    }
}