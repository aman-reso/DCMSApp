package com.management.org.dcms.codes.network.path

import com.google.gson.JsonObject
import com.management.org.dcms.codes.models.LoginRequestData
import com.management.org.dcms.codes.models.LoginResponseData
import org.json.JSONObject
import retrofit2.http.*
import javax.inject.Singleton


interface DcmsApiInterface {

    @Headers(value = ["Content-Type: application/json","Accept: application/json", "Cache-Control: max-age=640000"])
    @POST(value = "/api/sg/Login")
    suspend fun postLoginData(
        @Body loginRequestData: LoginRequestData
    ): LoginResponseData

}

