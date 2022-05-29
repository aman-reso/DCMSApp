package com.management.org.dcms.codes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.activity.LocationValue
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.repository.DcmsNetworkCallRepository
import com.management.org.dcms.codes.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UploadImageViewModel @Inject constructor(var repository: DcmsNetworkCallRepository) : ViewModel() {
    var liveDataForUploadImageResponse = MutableLiveData<GlobalNetResponse<*>>()

    internal fun uploadImageToServer(imagePart: MultipartBody.Part) {
        viewModelScope.launch(Dispatchers.IO) {
            if (Utility.isUserLoggedIn()) {
                val latitude: String = LocationValue.latitude
                val longitude: String = LocationValue.longitude
                val qId: String = "1"
                val hId: String = "1"
                val networkResponse = repository.uploadFileToServer(imagePart, AuthConfigManager.getAuthToken(), latitude, longitude, qId, hId)
                liveDataForUploadImageResponse.postValue(networkResponse)
            }
        }
    }
}