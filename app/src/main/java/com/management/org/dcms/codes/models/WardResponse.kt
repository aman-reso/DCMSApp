package com.management.org.dcms.codes.models

import com.management.org.dcms.codes.models.Ward
import com.squareup.moshi.Json

data class WardResponse(
    @Json(name = "Message")
    val Message:String,
    @Json(name = "status")
    val status:Int,
    @Json(name = "wards")
    val wards:List<Ward>
)
