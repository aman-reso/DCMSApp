package com.management.org.dcms.codes.network.path

import com.google.gson.JsonObject
import com.management.org.dcms.codes.models.LoginRequestData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {
    @FormUrlEncoded
    @Headers("Content-Type: application/json")
    @POST(value = "/swagger/ui/index#!/SG/SG_PostLogin")
    fun postLoginData(loginRequestData: LoginRequestData): Call<JSONObject>
}