package com.management.org.dcms.codes.dcmsclient.signup

import com.management.org.dcms.codes.dcmsclient.data.models.*
import com.management.org.dcms.codes.dcmsclient.data.network.SGService
import com.management.org.dcms.codes.dcmsclient.util.ResultWrapper
import com.management.org.dcms.codes.dcmsclient.util.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SignupRepository @Inject constructor(val sgService: SGService) {
    suspend fun userSignup(request: Register): ResultWrapper<RegisterResponse> {
        return safeApiCall(Dispatchers.IO) {
            sgService.userRegister(request)
        }
    }

    suspend fun getStateList(): ResultWrapper<StateListResponse> {
        return safeApiCall(Dispatchers.IO) {
            sgService.getStateList()
        }
    }

    suspend fun getDistrictList(stateId: Int): ResultWrapper<DistrictResponse> {
        return safeApiCall(Dispatchers.IO)
        { sgService.getDistrictByStateId(stateId) }
    }

    suspend fun getBlockList(id: Int): ResultWrapper<BlockResponse> {
        return safeApiCall(Dispatchers.IO) {
            sgService.getBlockByDistrictId(id)
        }
    }
    suspend fun getGpList(id:Int): ResultWrapper<GpResponse> {
       return safeApiCall(Dispatchers.IO){
            sgService.getGpById(id)
        }

    }

    suspend fun getVillageList(id:Int): ResultWrapper<VillageResponse> {
        return safeApiCall(Dispatchers.IO){
            sgService.getVillageId(id)
        }
    }

}