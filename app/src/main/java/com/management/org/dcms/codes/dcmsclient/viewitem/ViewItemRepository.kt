package com.management.org.dcms.codes.dcmsclient.viewitem

import com.management.org.dcms.codes.dcmsclient.data.models.HouseHoldsResponse
import com.management.org.dcms.codes.dcmsclient.data.network.SGService
import com.management.org.dcms.codes.dcmsclient.util.ResultWrapper
import com.management.org.dcms.codes.dcmsclient.util.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ViewItemRepository @Inject constructor(val sgService: SGService) {
    suspend fun getHouseholds(token:String): ResultWrapper<HouseHoldsResponse> {
        return safeApiCall(Dispatchers.IO){
            sgService.getHouseholds(token)
        }

    }
}