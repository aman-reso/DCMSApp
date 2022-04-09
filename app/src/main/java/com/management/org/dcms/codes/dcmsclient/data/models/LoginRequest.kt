package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "LoginId")
    val userName:String,
    @Json(name = "Password")
    val password:String,
    @Json(name = "DeviceId")
    val deviceId:String,
    @Json(name = "AndVersion")
    val andVersion:String,
    @Json(name = "IpAddress")
    val ipAddress:String,
    @Json(name = "Lattitude")
    val latitude:String,
    @Json(name = "Longitude")
    val longitude:String,

)
