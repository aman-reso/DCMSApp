package com.management.org.dcms.codes.models

data class LoginRequestData(
    var id: Int? = 0, val mobileNumber: String, val password: String,
    val deviceId: String,
    val androidVersion: String,
    val ipAddress: String, val latitude: String, var logitude: String
)

//Id (integer, optional),
//LoginId (string, optional),
//Password (string, optional),
//DeviceId (string, optional),
//AndVersion (string, optional),
//IpAddress (string, optional),
//Lattitude (string, optional),
//Longitude (string, optional)