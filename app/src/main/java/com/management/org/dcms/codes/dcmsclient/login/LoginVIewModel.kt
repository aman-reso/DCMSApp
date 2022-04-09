package com.management.org.dcms.codes.dcmsclient.login

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.dcmsclient.data.models.LoginRequest
import com.management.org.dcms.codes.dcmsclient.util.ResultWrapper
import com.management.org.dcms.codes.dcmsclient.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : ViewModel() {

    private val _loginStatus = MutableStateFlow<UiState>(UiState.Empty)
    val loginStatus = _loginStatus.asStateFlow()

    private val _logoutStatus  = MutableStateFlow<UiState>(UiState.Empty)
    val logoutStatus = _logoutStatus.asStateFlow()

    fun userLogin(
        userName: String,
        password: String,
        deviceId: String,
        ipAddress: String,
        latitude: String,
        longitude: String
    ) {

        val request = LoginRequest(
            userName,
            password,
            deviceId,
            Build.VERSION.SDK_INT.toString(),
            ipAddress,
            latitude,
            longitude
        )
        viewModelScope.launch {
            _loginStatus.value = UiState.Loading
            when (val response = repository.userLogin(request)) {
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                        _loginStatus.value = UiState.Failed(response.value.message)
                    } else {
                        _loginStatus.value = UiState.Success(response.value)
                    }
                }
                is ResultWrapper.GenericError -> {
                    _loginStatus.value = UiState.Failed("${response.error}")
                }
                is ResultWrapper.NetworkError -> {
                    _loginStatus.value = UiState.Failed("No Internet Connection")
                }


            }
        }
    }

    fun userLogout(token:String){
        viewModelScope.launch {
            when(val response = repository.userLogout(token)){
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                        _logoutStatus.value = UiState.Failed(response.value.message)
                    } else {
                        _logoutStatus.value = UiState.Success(response.value)
                    }
                }
                is ResultWrapper.GenericError -> {
                    _logoutStatus.value = UiState.Failed("${response.error}")
                }
                is ResultWrapper.NetworkError -> {
                    _logoutStatus.value = UiState.Failed("No Internet Connection")
                }
            }
        }
    }



}