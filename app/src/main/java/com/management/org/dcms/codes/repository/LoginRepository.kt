package com.management.org.dcms.codes.repository

import com.management.org.dcms.codes.models.LoginRequestData
import com.management.org.dcms.codes.network.path.DcmsApiInterface
import com.management.org.dcms.codes.network.path.safeApiCall
import com.management.org.dcms.codes.utility.AndroidDeviceUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(var apiInterface: DcmsApiInterface) {

    suspend fun submitRequestForLogin(inputPhone: String, inputPassword: String) = safeApiCall {
        val id: Int = 0
        val mobileNumber: String = inputPhone
        val password: String = inputPassword
        val deviceId: String = AndroidDeviceUtils.getDeviceId()
        val androidVersion: String = AndroidDeviceUtils.getAndroidVersion()
        val ipAddress: String = AndroidDeviceUtils.getLocalIpAddress()
        val latitude: String = "123"
        val longitude: String = "231"
        val loginRequestData = LoginRequestData(
            id, mobileNumber, password, deviceId, androidVersion,
            ipAddress, latitude, longitude
        )

        apiInterface.postLoginData(loginRequestData = loginRequestData)
    }

    suspend fun getTaskDetail(authToken: String) = safeApiCall {
        apiInterface.getTaskDetail(authToken)
    }

    suspend fun getWAMessageTemplate(authToken: String) = safeApiCall {
        apiInterface.getWaMessage(authToken)
    }

    suspend fun getContactsList(authToken: String) = safeApiCall {
        apiInterface.getContactsList(authToken)
    }
}