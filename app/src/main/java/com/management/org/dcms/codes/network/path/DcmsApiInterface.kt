package com.management.org.dcms.codes.network.path

import com.google.gson.JsonObject
import com.management.org.dcms.codes.models.*
import com.management.org.dcms.codes.viewmodel.MessageTemplateViewModel
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.http.*
import javax.inject.Singleton


interface DcmsApiInterface {

    @Headers(value = ["Content-Type: application/json", "Accept: application/json"])
    @POST(value = "/api/sg/Login")
    suspend fun postLoginData(
        @Body loginRequestData: LoginRequestData
    ): LoginResponseData

    @GET("/api/Task/Details")
    suspend fun getTaskDetail(@Query("AuthToken") token: String): TaskDetailsModel


    @GET("/api/SGAccount/Contacts")
    suspend fun getContactsList(@Query("AuthToken") authToken: String, @Query("CampaignId") CampaignId: Int, @Query("ThemeId") ThemeId: Int): ContactsMainModel


    @GET("/api/SGAccount/QContacts")
    suspend fun getQuestionContactsList(
        @Query("AuthToken") authToken: String, @Query("CampaignId") CampaignId: Int, @Query("ThemeId") ThemeId: Int
    ): QContactsMainModel


    @GET("/api/SGAccount/CallContacts")
    suspend fun getContactListForCall( @Query("AuthToken") authToken: String, @Query("CampaignId") CampaignId: Int, @Query("ThemeId") ThemeId: Int):QContactsMainModel

    @GET("/api/WA/Message")
    suspend fun getWaMessage(@Query("AuthToken") token: String): WAMessageTemplateModel

    @POST("/api/WA/SentReport")
    suspend fun sentWAReport(@Body sentReportPostModel: SentReportPostModel, @Query("AuthToken") authToken: String): JsonObject

    // http://dcmshost.dmi.ac.in/api/SGAccount/QContacts?AuthToken=wUESwWxNd20%3D&CampaignId=1&ThemeId=1
    @POST("/Api/Account/Profile")
    suspend fun getProfileDetails(@Query("AuthToken") authToken: String): JsonObject

    @POST("/api/sg/Logout")
    suspend fun logoutUserFromServer(@Query("AuthToken") authToken: String): LogoutResponseData


    @POST("/api/Question/SentReport")
    suspend fun sentReportQuestionActivity(@Body sentReportQActivityModel: SentReportQActivityModel, @Query("AuthToken") authToken: String): JsonObject

    @Headers(value = ["Accept: application/json"])
    @Multipart
    @POST("/api/Question/UploadQResponderImage")
    suspend fun uploadPicture(
        @Query("AuthToken") authToken: String, @Query("qid") qId: String, @Query("hhid") hhId: String, @Query("lat") lat: String, @Query("lang") lang: String, @Part part: MultipartBody.Part
    ): JsonObject

    @POST("/api/Account/PasswordChange")
    suspend fun changeUserPassword(
        @Query("AuthToken") authToken: String, @Query("oldpassword") oldPassword: String,
        @Query("newpassword") newPassword: String, @Query("confirmpassword") confirmPassword: String
    ): JsonObject


    @POST("/api/Account/ForgetPassword")
    suspend fun forgotUserPassword(@Query("mobileno") mobileNo: String, @Query("language") language: String? = "hi"): JsonObject


    @GET("/api/TextMsg/Message")
    suspend fun getTextMessageInfo(@Query("AuthToken") token: String): TextMessageTemplateModel

    @POST("/api/TextMsg/SentReport")
    suspend fun sentTextMessageReport(@Body sentReportPostModel: SentReportPostModel, @Query("AuthToken") authToken: String): JsonObject

    @POST("/api/TextMsg/GroupSentReport")
    suspend fun sentTextMessageGroupReport(@Body sentReportPostModel: SentReportInGroupModel, @Query("AuthToken") authToken: String): JsonObject

    @POST("/api/WA/GroupSentReport")
    suspend fun sentWAMessageGroupReport(@Body sentReportPostModel: SentReportInGroupModel, @Query("AuthToken") authToken: String): JsonObject

    @GET("api/Reports/QSentDetails")
    suspend fun getQSentDetails(@Query("AuthToken") authToken: String, @Query("fromdate") fromDate: String, @Query("todate") toDate: String): QReportDetailModel

    @GET("api/Reports/TextSentDetails")
    suspend fun getTextSentDetails(@Query("AuthToken") authToken: String, @Query("fromdate") fromDate: String, @Query("todate") toDate: String): TextReportDetailModel

    @GET("api/Reports/WASentDetails")
    suspend fun getWASentDetails(@Query("AuthToken") authToken: String, @Query("fromdate") fromDate: String, @Query("todate") toDate: String): WAReportDetailModel


    @POST("/api/CallLog/SentReport")
    suspend fun submitCallLog(
        @Query("AuthToken") authToken: String,
        @Query("DeviceId") deviceId: String,
        @Query("Lattitude") lattitude: String,
        @Query("Longitude") longitude: String,
        @Query("AndVersion") androidVersion: String,
        @Query("IpAddress") ipAddress: String,
        @Body list: ArrayList<UserCallLogsModel>
    ): JsonObject


    @Headers(value = ["Content-Type: application/json"])//Accept: application/json"
    @POST
    suspend fun makeApiCall(
        @Url url: String,
        @Body jsonObject: JsonObject
    ):JsonObject

    @POST("/api/CallLog/SentReport")
    suspend fun getCallLogsReport(
        @Query("AuthToken") authToken: String,
        @Query("DeviceId") deviceId: String,
        @Query("Lattitude") lattitude: String,
        @Query("Longitude") longitude: String,
        @Query("AndVersion") androidVersion: String,
        @Query("IpAddress") ipAddress: String,
    ):CallSentReportResponse

}

