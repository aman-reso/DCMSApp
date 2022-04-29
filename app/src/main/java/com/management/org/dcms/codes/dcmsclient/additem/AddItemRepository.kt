package com.management.org.dcms.codes.dcmsclient.additem

import com.management.org.dcms.codes.dcmsclient.data.models.HouseHold
import com.management.org.dcms.codes.dcmsclient.data.models.RegisterResponse
import com.management.org.dcms.codes.dcmsclient.data.models.VillageResponse
import com.management.org.dcms.codes.dcmsclient.data.models.WardResponse
import com.management.org.dcms.codes.dcmsclient.data.network.SGService
import com.management.org.dcms.codes.dcmsclient.util.ResultWrapper
import com.management.org.dcms.codes.dcmsclient.util.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class AddItemRepository @Inject constructor (private val sgService: SGService) {
    suspend fun getAllVillages(): ResultWrapper<VillageResponse> {
        return safeApiCall(Dispatchers.IO) {
            sgService.getAllVillageList()
        }
    }

    suspend fun getAllVillagesBySg(token:String): ResultWrapper<VillageResponse> {
        return safeApiCall(Dispatchers.IO) {
            sgService.getVillageBySg(token)
        }
    }
    suspend fun registerHousehold(request: HouseHold, authToken:String): ResultWrapper<RegisterResponse> {
        return safeApiCall(Dispatchers.IO){
            sgService.householdRegister(request,authToken)
        }

    }

    suspend fun getWardByVillageId(id:Int): ResultWrapper<WardResponse> {
       return safeApiCall(Dispatchers.IO){
            sgService.getWardByVillageId(id)
        }
    }

}