package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "Message")
    val message:String,
    @Json(name = "Status")
    val status:Int
)
