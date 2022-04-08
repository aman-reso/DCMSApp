package com.management.org.dcms.codes.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "Message")
    val Message:String,
    @Json(name = "Status")
    val Status:Int
)
