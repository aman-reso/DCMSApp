package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class HouseHoldsResponse(
    @Json(name = "Message")
    val message:String,
    @Json(name = "status")
    val status:Int,
    @Json(name = "HouseHolds")
    val households:List<HouseHold>
)
