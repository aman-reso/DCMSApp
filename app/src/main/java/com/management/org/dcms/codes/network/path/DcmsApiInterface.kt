package com.management.org.dcms.codes.network.path

import com.google.gson.JsonObject
import com.management.org.dcms.codes.models.*
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

    @GET("/api/WA/Message")
    suspend fun getWaMessage(@Query("AuthToken") token: String): WAMessageTemplateModel

    @GET("/api/SGAccount/Contacts")
    suspend fun getContactsList(@Query("AuthToken") authToken: String, @Query("CampaignId") CampaignId: Int, @Query("ThemeId") ThemeId: Int): ContactsMainModel

    @POST("/api/sg/Logout")
    suspend fun logoutUserFromServer(@Query("AuthToken") authToken: String): LogoutResponseData

    @POST("/api/WA/SentReport")
    suspend fun sentWAResponseReport(@Body sentReportPostModel: SentReportPostModel, @Query("AuthToken") authToken: String): JsonObject

    @GET("/api/SGAccount/QContacts")
    suspend fun getQuestionContactsList(
        @Query("AuthToken") authToken: String, @Query("CampaignId") CampaignId: Int, @Query("ThemeId") ThemeId: Int
    ): QContactsMainModel

   // http://dcmshost.dmi.ac.in/api/SGAccount/QContacts?AuthToken=wUESwWxNd20%3D&CampaignId=1&ThemeId=1


}

