package com.management.org.dcms.codes.viewmodel.logintvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.LoginResponseData
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(var loginRepository: LoginRepository) : ViewModel() {

    fun submitLoginData(inputPhone: String, inputPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = loginRepository.submitRequestForLogin(inputPhone, inputPassword)) {
                is GlobalNetResponse.NetworkFailure -> {

                }
                is GlobalNetResponse.Success -> {
                    val loginResponseData: LoginResponseData? = response.value
                    if (loginResponseData?.authToken != null) {
                        AuthConfigManager.saveAuthToken(loginResponseData.authToken)
                      System.out.println("authToken-->"+  AuthConfigManager.getAuthToken())
                    }
                }
            }
        }
    }
}