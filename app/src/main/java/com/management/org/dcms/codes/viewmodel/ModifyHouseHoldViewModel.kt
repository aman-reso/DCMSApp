package com.management.org.dcms.codes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.DcmsNetworkCallRepository
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyHouseHoldViewModel @Inject constructor(val repository: DcmsNetworkCallRepository):ViewModel() {
    internal var houseHoldDetailsLiveData=MutableLiveData<GlobalNetResponse<JsonObject>?>()
    internal fun getHouseHoldDetailsById(hhId:String)=viewModelScope.launch {
        if (Utility.isUserLoggedIn()){
            val authToken=AuthConfigManager.getAuthToken()
            repository.getHouseHoldDetailsBasedOnId(hhId,authToken)
        }
    }
}