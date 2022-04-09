package com.management.org.dcms.codes.dcmsclient.signup

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.dcmsclient.data.models.Register
import com.management.org.dcms.codes.dcmsclient.util.ResultWrapper
import com.management.org.dcms.codes.dcmsclient.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val repository: SignupRepository) : ViewModel() {

    private val _stateList = MutableStateFlow<UiState>(UiState.Empty)
    val stateList = _stateList.asStateFlow()

    private val _districtList = MutableStateFlow<UiState>(UiState.Empty)
    val districtList = _districtList.asStateFlow()

    private val _blockList = MutableStateFlow<UiState>(UiState.Empty)
    val blockList = _blockList.asStateFlow()

    private val _gpList = MutableStateFlow<UiState>(UiState.Empty)
    val gpList = _gpList.asStateFlow()

    private val _villageList = MutableStateFlow<UiState>(UiState.Empty)
    val villageList = _villageList.asStateFlow()


    private val _signupResponse = MutableStateFlow<UiState>(UiState.Empty)
    val signupResponse = _signupResponse.asStateFlow()

    fun userRegister(stateId: Int,districtId:Int,blockId:Int,gpId:Int,villageId:Int,username:String,password:String,email:String,mobile:String,deviceId:String,latitude:String,longitude:String) {
        val request = Register(
            stateId = stateId,
            districtId = districtId,
            blockId = blockId,
            villageId = villageId,
            userName = username,
            password = password,
            emailId = email,
            mobileNUmber = mobile,
            deviceId = deviceId,
            latitude = latitude,
            longitude = longitude,
            andVersion = Build.VERSION.SDK_INT.toString(),
            gpId = gpId
        )
        viewModelScope.launch {
            when (val response = repository.userSignup(request)) {
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                       _signupResponse.value =  UiState.Failed(response.value.message)
                    } else {
                        _signupResponse.value =  UiState.Success(response.value.message)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _signupResponse.value = UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    _signupResponse.value = UiState.Failed(response.error.toString())

                }

            }
        }
    }

    fun getStateList() {
        _stateList.value = UiState.Loading
        viewModelScope.launch {
            when (val response = repository.getStateList()) {
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                        _stateList.value = UiState.Failed(response.value.message)
                    } else {
                        _stateList.value = UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _stateList.value = UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    _stateList.value = UiState.Failed(response.error.toString())

                }
            }

        }
    }

    fun specificDistrict(stateId: Int) {
        _districtList.value = UiState.Loading
        viewModelScope.launch {
            when (val response = repository.getDistrictList(stateId)) {
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                        _districtList.value = UiState.Failed(response.value.message)
                    } else {
                        _districtList.value = UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _districtList.value = UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    _districtList.value = UiState.Failed(response.error.toString())

                }
            }
        }
    }


    fun getBlockById(id: Int) {
        _blockList.value = UiState.Loading
        viewModelScope.launch {
            when (val response = repository.getBlockList(id)) {
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                        _blockList.value = UiState.Failed(response.value.message)
                    } else {
                        _blockList.value = UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _blockList.value = UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    _blockList.value = UiState.Failed(response.error.toString())

                }
            }
        }
    }

    fun getGpById(id: Int) {
        _gpList.value = UiState.Loading
        viewModelScope.launch {
            when (val response = repository.getGpList(id)) {
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                        _gpList.value = UiState.Failed(response.value.message)
                    } else {
                        _gpList.value = UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _gpList.value = UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    _gpList.value = UiState.Failed(response.error.toString())

                }
            }
        }
    }

    fun getVillageBId(id: Int){
        _villageList.value = UiState.Loading
        viewModelScope.launch {
            when(val response = repository.getVillageList(id)){
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                      _villageList.value =   UiState.Failed(response.value.message)
                    } else {
                        _villageList.value =   UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _villageList.value =  UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    _villageList.value =  UiState.Failed(response.error.toString())

                }

            }

        }
    }


}