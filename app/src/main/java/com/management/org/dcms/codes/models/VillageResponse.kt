package com.management.org.dcms.codes.models

import com.management.org.dcms.codes.models.Village
import com.squareup.moshi.Json

data class VillageResponse(
    @Json(name = "Message")
    val Message:String,
    @Json(name = "status")
    val status:Int,
    @Json(name = "Villages")
    val Villages:List<Village>
)