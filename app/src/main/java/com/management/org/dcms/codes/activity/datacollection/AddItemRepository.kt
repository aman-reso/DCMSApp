package com.management.org.dcms.codes.activity.datacollection

import com.management.org.dcms.codes.models.RegisterResponse
import com.management.org.dcms.codes.models.VillageResponse
import com.management.org.dcms.codes.models.WardResponse
import com.management.org.dcms.codes.models.houseHoldView
import com.management.org.dcms.codes.network.path.DcmsApiInterface
import com.management.org.dcms.codes.network.path.ResultWrapper
import com.management.org.dcms.codes.network.path.safeApiCallNew
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class AddItemRepository @Inject constructor (var apiInterface: DcmsApiInterface) {
    suspend fun getAllVillages(): ResultWrapper<VillageResponse> {
        return safeApiCallNew(Dispatchers.IO) {
            apiInterface.getAllVillageList()
        }
    }

    suspend fun getAllVillagesBySg(token:String): ResultWrapper<VillageResponse> {
        return safeApiCallNew(Dispatchers.IO) {
            apiInterface.getVillageBySg(token)
        }
    }
    suspend fun registerHousehold(request: houseHoldView, authToken:String): ResultWrapper<RegisterResponse> {
        return safeApiCallNew(Dispatchers.IO){
            apiInterface.householdRegister(request,authToken)
        }

    }

    suspend fun getWardByVillageId(id:Int): ResultWrapper<WardResponse> {
       return safeApiCallNew(Dispatchers.IO){
            apiInterface.getWardByVillageId(id)
        }
    }

}