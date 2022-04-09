package com.management.org.dcms.codes.dcmsclient.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.dcmsclient.data.models.HouseHold
import com.management.org.dcms.codes.dcmsclient.util.ResultWrapper
import com.management.org.dcms.codes.dcmsclient.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddItemViewModel @Inject constructor(private val repository: AddItemRepository) :
    ViewModel() {

    private val _allVillages = MutableStateFlow<UiState>(UiState.Empty)
    val allVillages = _allVillages.asStateFlow()

    private val _wardById = MutableStateFlow<UiState>(UiState.Empty)
    val wardById = _wardById.asStateFlow()

    private val _registerResponse = MutableStateFlow<UiState>(UiState.Empty)
    val registerResponse = _registerResponse.asStateFlow()

    fun getAllVillages(token: String) {
        _allVillages.value = UiState.Loading
        viewModelScope.launch {
            when (val response = repository.getAllVillagesBySg(token)) {
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                      _allVillages.value =   UiState.Failed(response.value.message)
                    } else {
                        _allVillages.value =   UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _allVillages.value = UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    _allVillages.value =  UiState.Failed(response.error.toString())

                }

            }
        }
    }

    fun registerHousehold(
        villageId: Int,
        villageName: String,
        name: String,
        mobile: String,
        whatsapp: String,
        email: String,
        address: String,
        father: String,
        mother: String,
        ward: String,
        landMark: String,
        token:String,
        latitude:String,
        longitude:String,
        image:String,
        extension:String,
        mobileType:String,
        documentNumber:String,
        documentType:String
    ) {
        val request = HouseHold(
            villageId = villageId,
            villageName = villageName,
            name =name,
            mobileNumber =mobile,
            whatsappNumber =whatsapp,
            emailId =email,
            address =address,
            fatherName =father,
            motherName =mother,
            wardNumber =ward,
            landmark =landMark,
            latitude = latitude,
            longitude = longitude,
            imageBase64 = image,
            extension = extension,
            mobileType = mobileType,
            documentNumber = documentNumber,
            documentType = documentType
        )
        viewModelScope.launch {
            _registerResponse.value = UiState.Loading
            when (val response = repository.registerHousehold(request,token)) {
                is ResultWrapper.Success -> {
                    System.out.println("respone--."+response.value.message)
                    if (response.value.status == 0) {
                       _registerResponse.value =  UiState.Failed(response.value.message)
                    } else {
                        _registerResponse.value =  UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _registerResponse.value =  UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    System.out.println("respone--."+response.error)

                    _registerResponse.value =  UiState.Failed(response.error.toString())

                }
            }
        }
    }

    fun getWardById(id:Int){
        _wardById.value = UiState.Loading
        viewModelScope.launch {
            when (val response = repository.getWardByVillageId(id)) {
                is ResultWrapper.Success -> {
                    if (response.value.status == 0) {
                        _wardById.value =   UiState.Failed(response.value.message)
                    } else {
                        _wardById.value =   UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError -> {
                    _wardById.value = UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError -> {
                    _wardById.value =  UiState.Failed(response.error.toString())

                }

            }
        }
    }
}