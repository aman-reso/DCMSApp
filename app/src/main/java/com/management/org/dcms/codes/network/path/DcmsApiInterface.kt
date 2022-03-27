package com.management.org.dcms.codes.network.path

import com.google.gson.JsonObject
import retrofit2.http.POST
import retrofit2.http.Path

interface DcmsApiInterface {
    @POST(value = "/swagger/ui/index#!/SG/SG_PostLogin")
    suspend fun postLoginData(): JsonObject
}

//https://fms.farmserp.in/swagger/ui/index#!/SG/SG_PostLogin