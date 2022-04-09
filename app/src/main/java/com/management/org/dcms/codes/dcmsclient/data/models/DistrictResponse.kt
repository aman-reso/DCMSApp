package com.management.org.dcms.codes.dcmsclient.data.models

import com.management.org.dcms.codes.dcmsclient.data.models.District
import com.squareup.moshi.Json

class DistrictResponse(
    @Json(name = "Message")
    val message:String,
    @Json(name = "status")
    val status:Int,
    @Json(name = "Districts")
    val districts:List<District>
)