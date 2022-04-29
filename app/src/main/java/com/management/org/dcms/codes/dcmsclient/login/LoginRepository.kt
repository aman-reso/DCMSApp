package com.management.org.dcms.codes.dcmsclient.login

import com.management.org.dcms.codes.dcmsclient.data.models.LoginRequest
import com.management.org.dcms.codes.dcmsclient.data.models.LoginResponse
import com.management.org.dcms.codes.dcmsclient.data.network.SGService
import com.management.org.dcms.codes.dcmsclient.util.ResultWrapper
import com.management.org.dcms.codes.dcmsclient.util.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class LoginRepository @Inject constructor(private val sgService: SGService) {
    suspend fun userLogin(request: LoginRequest): ResultWrapper<LoginResponse> {
       return safeApiCall(Dispatchers.IO){
            sgService.userLogin(request)
        }
    }

    suspend fun userLogout(token:String): ResultWrapper<LoginResponse> {
        return safeApiCall(Dispatchers.IO){
            sgService.userLogout(token
            )
        }
    }

}