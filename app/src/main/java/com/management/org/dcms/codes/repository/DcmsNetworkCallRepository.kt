package com.management.org.dcms.codes.repository

import com.google.gson.JsonObject
import com.management.org.dcms.codes.activity.LocationValue
import com.management.org.dcms.codes.models.*
import com.management.org.dcms.codes.network.path.DcmsApiInterface
import com.management.org.dcms.codes.network.path.safeApiCall
import com.management.org.dcms.codes.utility.AndroidDeviceUtils
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class
DcmsNetworkCallRepository @Inject constructor(var apiInterface: DcmsApiInterface) {

    suspend fun submitRequestForLogin(inputPhone: String, inputPassword: String) = safeApiCall {
        val id: Int = 0
        val mobileNumber: String = inputPhone
        val password: String = inputPassword
        val deviceId: String = AndroidDeviceUtils.getDeviceId()
        val androidVersion: String = AndroidDeviceUtils.getAndroidVersion()
        val ipAddress: String = AndroidDeviceUtils.getLocalIpAddress()
        val latitude: String = LocationValue.latitude
        val longitude: String = LocationValue.longitude
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
        apiInterface.getContactsList(authToken, themeId, campaignId)
    }

    suspend fun logoutUserFromServer(authToken: String) = safeApiCall {
        apiInterface.logoutUserFromServer(authToken = authToken)
    }

    suspend fun sentWAReport(authToken: String, hhId: Int, templateId: Int, waNum: String) = safeApiCall {
        val deviceId: String = AndroidDeviceUtils.getDeviceId()
        val androidVersion: String = AndroidDeviceUtils.getAndroidVersion()
        val ipAddress: String = AndroidDeviceUtils.getLocalIpAddress()
        val latitude: String = LocationValue.latitude
        val longitude: String = LocationValue.longitude
        val sentReportPostModel = SentReportPostModel(HHId = hhId, WANo = waNum, TemplateId = templateId, deviceId, latitude, longitude, androidVersion, ipAddress)
        apiInterface.sentWAReport(authToken = authToken, sentReportPostModel = sentReportPostModel)
    }

    suspend fun getContactsListForQuestion(authToken: String, campaignId: Int, themeId: Int) = safeApiCall {
        apiInterface.getQuestionContactsList(authToken, campaignId, themeId)
    }
    suspend fun getContactListForCall(authToken: String, campaignId: Int, themeId: Int) = safeApiCall {
        apiInterface.getContactListForCall(authToken, campaignId, themeId)
    }

    suspend fun getProfileDetails(authToken: String) = safeApiCall {
        apiInterface.getProfileDetails(authToken = authToken)
    }

    suspend fun sentReportQuestionActivity(sentReportQActivityModel: SentReportQActivityModel, authToken: String) = safeApiCall {
        apiInterface.sentReportQuestionActivity(sentReportQActivityModel, authToken)
    }

    suspend fun uploadFileToServer(imagePart: MultipartBody.Part, authToken: String, lat: String, lang: String, qId: String, hId: String) = safeApiCall {
        apiInterface.uploadPicture(authToken = authToken, lat = lat, lang = lang, qId = qId, hhId = hId, part = imagePart)
    }

    suspend fun changeUserPassword(authToken: String, oldPassword: String, newPassword: String, confirmPassword: String) = safeApiCall {
        apiInterface.changeUserPassword(authToken = authToken, oldPassword = oldPassword, newPassword = newPassword, confirmPassword = confirmPassword)
    }

    suspend fun forgotUserPassword(mobileNo: String) = safeApiCall {
        apiInterface.forgotUserPassword(mobileNo = mobileNo)
    }

    suspend fun sentTextSmsReport(authToken: String, hhId: Int, templateId: Int, waNum: String) = safeApiCall {
        val deviceId: String = AndroidDeviceUtils.getDeviceId()
        val androidVersion: String = AndroidDeviceUtils.getAndroidVersion()
        val ipAddress: String = AndroidDeviceUtils.getLocalIpAddress()
        val latitude: String = LocationValue.latitude
        val longitude: String = LocationValue.longitude
        val sentReportPostModel = SentReportPostModel(HHId = hhId, WANo = waNum, TemplateId = templateId, deviceId, latitude, longitude, androidVersion, ipAddress)
        apiInterface.sentTextMessageReport(authToken = authToken, sentReportPostModel = sentReportPostModel)
    }

    suspend fun getTextSMSTemplate(authToken: String) = safeApiCall {
        apiInterface.getTextMessageInfo(authToken)
    }

    suspend fun sentTextSMSGroupReport(authToken: String, sentReportInGroupModel: SentReportInGroupModel) = safeApiCall {
        apiInterface.sentTextMessageGroupReport(authToken = authToken, sentReportPostModel = sentReportInGroupModel)
    }

    suspend fun sentWAGroupReport(authToken: String, sentReportInGroupModel: SentReportInGroupModel) = safeApiCall {
        apiInterface.sentWAMessageGroupReport(authToken = authToken, sentReportPostModel = sentReportInGroupModel)
    }

    suspend fun getQSentDetails(authToken: String, fromDate: String, toDate: String) = safeApiCall {
        apiInterface.getQSentDetails(authToken, fromDate, toDate)
    }

    suspend fun getWASentDetails(authToken: String, fromDate: String, toDate: String) = safeApiCall {
        apiInterface.getWASentDetails(authToken, fromDate, toDate)
    }

    suspend fun getTextSentDetails(authToken: String, fromDate: String, toDate: String) = safeApiCall {
        apiInterface.getTextSentDetails(authToken, fromDate, toDate)
    }

    suspend fun submitCallReport(authToken: String, list: ArrayList<UserCallLogsModel>, hhId: Int) = safeApiCall {
        val deviceId: String = AndroidDeviceUtils.getDeviceId()
        val androidVersion: String = AndroidDeviceUtils.getAndroidVersion()
        val ipAddress: String = AndroidDeviceUtils.getLocalIpAddress()
        val latitude: String = LocationValue.latitude
        val longitude: String = LocationValue.longitude
        apiInterface.submitCallLog(authToken, deviceId, latitude, longitude, androidVersion, ipAddress,  list)
    }
    suspend fun makeApiCall(obj: JsonObject) = safeApiCall {
        val url="https://railsinfo-services.makemytrip.com/api/rails/pnr/currentstatus/v1?region=in&language=eng&currency=inr"
        apiInterface.makeApiCall(url,obj)
    }
    suspend fun getCallLogsReport(authToken: String)= safeApiCall{
        val deviceId: String = AndroidDeviceUtils.getDeviceId()
        val androidVersion: String = AndroidDeviceUtils.getAndroidVersion()
        val ipAddress: String = AndroidDeviceUtils.getLocalIpAddress()
        val latitude: String = LocationValue.latitude
        val longitude: String = LocationValue.longitude
        apiInterface.getCallLogsReport(authToken, deviceId, latitude, longitude, androidVersion, ipAddress)
    }

}