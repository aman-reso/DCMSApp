package com.management.org.dcms.codes.network.path

import com.management.org.dcms.codes.models.*
import retrofit2.http.*


interface DcmsApiInterface {

    @Headers(value = ["Content-Type: application/json", "Accept: application/json", "Cache-Control: max-age=640000"])
    @POST(value = "/api/sg/Login")
    suspend fun postLoginData(
        @Body loginRequestData: LoginRequestData
    ): LoginResponseData

    @GET("/api/Task/Details")
    suspend fun getTaskDetail(@Query("AuthToken") token: String): TaskDetailsModel

    @GET("/api/WA/Message")
    suspend fun getWaMessage(@Query("AuthToken") token: String): WAMessageTemplateModel

    @GET("/api/SGAccount/Contacts")
    suspend fun getContactsList(@Query("AuthToken") authToken: String): ContactsMainModel

    @POST("/api/sg/Logout")
    suspend fun logoutUserFromServer(@Query("AuthToken") authToken: String): LogoutResponseData

    @GET("/api/ward/WardByVillageId")
    suspend fun getWardByVillageId (@Query("id")id:Int): WardResponse

    @GET("/api/village/VillagesBySg")
    suspend fun getVillageBySg(@Query("AuthToken")token:String):VillageResponse

    @GET("/api/village/list")
    suspend fun getAllVillageList():VillageResponse

    @POST("/api/households/register")
    suspend fun householdRegister(
        @Body request: houseHoldView,
        @Query("AuthToken") token: String,
    ): RegisterResponse
}

