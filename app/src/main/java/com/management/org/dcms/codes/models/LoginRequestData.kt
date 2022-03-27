package com.management.org.dcms.codes.models

import com.google.gson.annotations.SerializedName

data class LoginRequestData(
    @SerializedName("Id")
    var id: Int? = 0,

    @SerializedName("LoginId")
    val mobileNumber: String,

    @SerializedName("Password")
    val password: String,

    @SerializedName("DeviceId")
    val deviceId: String,

    @SerializedName("AndVersion")
    val androidVersion: String,

    @SerializedName("IpAddress")
    val ipAddress: String,

    @SerializedName("Lattitude")
    val latitude: String,

    @SerializedName("Longitude")
    var longitude: String
)
