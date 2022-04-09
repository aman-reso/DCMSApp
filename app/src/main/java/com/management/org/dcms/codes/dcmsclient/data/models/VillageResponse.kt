package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class VillageResponse(
    @Json(name = "Message")
    val message:String,
    @Json(name = "status")
    val status:Int,
    @Json(name = "Villages")
    val villages:List<Village>
)