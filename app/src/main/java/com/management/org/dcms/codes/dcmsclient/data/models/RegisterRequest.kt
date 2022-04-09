package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class Register(
    @Json(name = "Id")
    val id: Int? = null,
    @Json(name = "UserCode")
    val userCode: String = "",
    @Json(name = "StateId")
    val stateId: Int,
    @Json(name = "DistrictId")
    val districtId: Int,
    @Json(name = "BlockId")
    val blockId: Int,
    @Json(name = "GramPanchayatId")
    val gpId:Int,
    @Json(name = "VillageId")
    val villageId: Int,
    @Json(name = "UserName")
    val userName: String,
    @Json(name = "Password")
    val password: String,
    @Json(name = "EmailId")
    val emailId: String,
    @Json(name = "MobileNo")
    val mobileNUmber: String,
    @Json(name = "RoleId")
    val roleId: Int? = 0,
    @Json(name = "IsApproved")
    val isApproved: Boolean? = null,
    @Json(name = "IsActive")
    val isActive: Boolean? = true,
    @Json(name = "IsEmailVerified")
    val isEmailVerified: Boolean? = true,
    @Json(name = "IsMobileVerified")
    val isMobileVerified: Boolean? = true,
    @Json(name = "IsLocked")
    val isLocked: Boolean? = true,
    @Json(name = "IsPasswordChanged")
    val isPasswordChanged: Boolean? = true,
    @Json(name = "VillageName")
    val villageName: String? = "",
    @Json(name = "DistrictName")
    val districtName: String? = "",
    @Json(name = "BlockName")
    val blockName: String? = "",
    @Json(name = "GramPanchayatName")
    val gramPanchayatName: String? = "",
    @Json(name = "DeviceId")
    val deviceId: String,
    @Json(name = "Lattitude")
    val latitude: String,
    @Json(name = "Longitude")
    val longitude: String,
    @Json(name = "AndVersion")
    val andVersion: String
)
