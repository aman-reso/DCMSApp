package com.management.org.dcms.codes.repository

import com.management.org.dcms.codes.models.LoginRequestData
import com.management.org.dcms.codes.models.SentReportPostModel
import com.management.org.dcms.codes.network.path.DcmsApiInterface
import com.management.org.dcms.codes.network.path.safeApiCall
import com.management.org.dcms.codes.utility.AndroidDeviceUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DcmsNetworkCallRepository @Inject constructor(var apiInterface: DcmsApiInterface) {

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
        System.out.println("authTOken-->$authToken")
        apiInterface.getTaskDetail(authToken)
    }

    suspend fun getWAMessageTemplate(authToken: String) = safeApiCall {
        apiInterface.getWaMessage(authToken)
    }

    suspend fun getContactsList(authToken: String, themeId: Int, campaignId: Int) = safeApiCall {
        apiInterface.getContactsList(authToken,themeId,campaignId)
    }

    suspend fun logoutUserFromServer(authToken: String) = safeApiCall {
        apiInterface.logoutUserFromServer(authToken = authToken)
    }

    suspend fun sentWAReport(authToken: String, hhId: Int, templateId: Int, waNum: String) = safeApiCall {
        val deviceId: String = AndroidDeviceUtils.getDeviceId()
        val androidVersion: String = AndroidDeviceUtils.getAndroidVersion()
        val ipAddress: String = AndroidDeviceUtils.getLocalIpAddress()
        val latitude: String = "123"
        val longitude: String = "231"
        val sentReportPostModel = SentReportPostModel(HHId = hhId, WANo = waNum, TemplateId = templateId, deviceId, latitude, longitude, androidVersion, ipAddress)
        apiInterface.sentWAResponseReport(authToken = authToken, sentReportPostModel = sentReportPostModel)
    }
    suspend fun getContactsListForQuestion(authToken: String,campaignId: Int,themeId: Int)= safeApiCall {
        apiInterface.getQuestionContactsList(authToken,campaignId,themeId)
    }
    suspend fun getProfileDetails(authToken: String)= safeApiCall {
        apiInterface.getProfileDetails(authToken = authToken)
    }
}