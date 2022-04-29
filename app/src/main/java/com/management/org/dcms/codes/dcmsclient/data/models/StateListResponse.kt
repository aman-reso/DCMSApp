package com.management.org.dcms.codes.dcmsclient.data.models

import com.management.org.dcms.codes.dcmsclient.data.models.State
import com.squareup.moshi.Json

data class StateListResponse(
    @Json(name = "Message")
    val message:String,
    @Json(name = "status")
    val status:Int,
    @Json(name = "States")
    val states:List<State>
)
