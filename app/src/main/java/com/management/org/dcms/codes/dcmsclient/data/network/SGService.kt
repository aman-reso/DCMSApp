package com.management.org.dcms.codes.dcmsclient.data.network

import com.management.org.dcms.codes.dcmsclient.data.models.*
import retrofit2.http.*

interface SGService {
    @POST("/api/sg/Login")
    suspend fun userLogin(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("/api/sg/Register")
    suspend fun userRegister(@Body request: Register): RegisterResponse

    @POST("/api/households/register")
    suspend fun householdRegister(
        @Body request: HouseHold,
        @Query("AuthToken") token: String,
    ): RegisterResponse

    @GET("/api/households/details")
    suspend fun getHouseholds(@Query("AuthToken") token: String): HouseHoldsResponse

    @GET("/api/sg/allregisters")
    suspend fun getAllRegisters(@Query("AuthToken") token: String): AllRegisters

    @GET("/api/sg/ViewById")
    suspend fun getRegister(@Query("id") id: Int, @Query("AuthToken") token: String)

    @GET("/api/state/list")
    suspend fun getStateList(): StateListResponse

    @GET("/api/district/DistrictByStateId")
    suspend fun getDistrictByStateId(@Query("id")stateId:Int): DistrictResponse

    @GET("/api/block/BlockByDistrictId")
    suspend fun getBlockByDistrictId(@Query("id")districtId:Int): BlockResponse

    @GET ("/api/gp/GPByBlockId")
    suspend fun getGpById(@Query("id")id:Int): GpResponse

    @GET ("/api/village/VillageByGPId")
    suspend fun getVillageId(@Query("id")id:Int): VillageResponse

    @GET("/api/village/list")
    suspend fun getAllVillageList(): VillageResponse

    @POST("/api/sg/logout")
    suspend fun userLogout(@Query("AuthToken")token:String): LoginResponse


    @GET("/api/ward/WardByVillageId")
    suspend fun getWardByVillageId (@Query("id")id:Int): WardResponse

    @GET(" /api/village/VillagesBySg")
    suspend fun getVillageBySg(@Query("AuthToken")token:String): VillageResponse


}