package com.management.org.dcms.codes.dcmsclient.viewitem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.management.org.dcms.codes.dcmsclient.util.ResultWrapper
import com.management.org.dcms.codes.dcmsclient.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewItemVIewModel @Inject constructor (private val repository: ViewItemRepository):ViewModel() {

    private val _householdList = MutableStateFlow<UiState>(UiState.Empty)
    val householdList = _householdList.asStateFlow()

    fun getHouseholds(token:String){
        _householdList.value = UiState.Loading
        viewModelScope.launch {
            when(val response = repository.getHouseholds(token)){
                is ResultWrapper.Success ->{
                    if(response.value.status == 0){
                      _householdList.value =   UiState.Failed(response.value.message)
                    }
                    else{
                        _householdList.value =   UiState.Success(response.value)
                    }
                }
                is ResultWrapper.NetworkError->{
                    _householdList.value =  UiState.Failed("No Internet")
                }
                is ResultWrapper.GenericError-> {
                    _householdList.value =     UiState.Failed(response.error?.error)

                }

            }
        }
    }
}